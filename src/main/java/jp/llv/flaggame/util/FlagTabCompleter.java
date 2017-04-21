/*
 * Copyright (C) 2017 SakuraServerDev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jp.llv.flaggame.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.command.CommandSender;
import syam.flaggame.FlagGame;
import syam.flaggame.exception.FlagGameException;

/**
 *
 * @author SakuraServerDev
 */
@FunctionalInterface
public interface FlagTabCompleter {

    Collection<String> complete(FlagGame plugin, List<String> args, CommandSender sender)
            throws FlagGameException;
    
    static Builder builder() {
        return new Builder();
    }

    static final class Builder {

        private final List<CollectionSuggestionProvider> providers = new ArrayList<>();
        private CollectionSuggestionProvider defaultProvider = null;

        private Builder() {
        }

        private void setProvider(int index, CollectionSuggestionProvider provider) {
            Objects.requireNonNull(provider);
            if (index < providers.size()) {
                providers.set(index, provider);
            } else {
                for (int i = providers.size(); i < index; i++) {
                    providers.add(null);
                }
                providers.add(provider);
            }
        }

        private void setProvider(CollectionSuggestionProvider provider) {
            Objects.requireNonNull(provider);
            defaultProvider = provider;
        }

        public SuggestionProviderConsumer forArg(int index) {
            if (index < 0) {
                throw new IllegalArgumentException();
            }
            return new SuggestionProviderConsumer(index);
        }

        public SuggestionProviderConsumer forOtherArgs() {
            return new SuggestionProviderConsumer();
        }

        public FlagTabCompleter create() {
            final CollectionSuggestionProvider finalDefaultProvider = defaultProvider;
            final List<CollectionSuggestionProvider> finalProviders
                    = Collections.unmodifiableList(new ArrayList<>(providers));
            return (plugin, args, sender) -> {
                if (args.size() < finalProviders.size()) {
                    CollectionSuggestionProvider provider = finalProviders.get(args.size());
                    if (provider == null) {
                        provider = finalDefaultProvider;
                    }
                    if (provider != null) {
                        return provider.get(plugin, sender, args.toArray(new String[args.size()]))
                                .stream()
                                .map(Objects::toString)
                                .filter(s -> s.startsWith(args.get(args.size() - 1)))
                                .collect(Collectors.toList());
                    }
                }
                return Collections.emptyList();
            };
        }

        public class SuggestionProviderConsumer {

            private final Integer index;

            private SuggestionProviderConsumer() {
                this.index = null;
            }

            private SuggestionProviderConsumer(int index) {
                this.index = index;
            }

            public Builder suggest(CollectionSuggestionProvider provider) {
                if (index == null) {
                    setProvider(provider);
                } else {
                    setProvider(index, provider);
                }
                return Builder.this;
            }

            public Builder suggest(ArraySuggestionProvider provider) {
                return suggest(provider.toCollectionSuggestionProvider());
            }

            public Builder suggest(StreamSuggestionProvider provider) {
                return suggest(provider.toCollectionSuggestionProvider());
            }

            public Builder suggest(Class<? extends Enum> suggestion) {
                return suggest((ArraySuggestionProvider) (p, s, a) -> suggestion.getEnumConstants());
            }

            public Builder suggest(String... suggestion) {
                return suggest((ArraySuggestionProvider) (p, s, a) -> suggestion);
            }

        }

        @FunctionalInterface
        public static interface CollectionSuggestionProvider {

            Collection<? extends Object> get(FlagGame plugin, CommandSender sender, String[] args);

        }

        @FunctionalInterface
        public static interface ArraySuggestionProvider {

            Object[] get(FlagGame plugin, CommandSender sender, String[] args);

            default CollectionSuggestionProvider toCollectionSuggestionProvider() {
                return (p, s, a) -> Arrays.asList(get(p, s, a));
            }

        }

        @FunctionalInterface
        public static interface StreamSuggestionProvider {

            Stream<? extends Object> get(FlagGame plugin, CommandSender sender, String[] args);

            default CollectionSuggestionProvider toCollectionSuggestionProvider() {
                return (p, s, a) -> get(p, s, a).collect(Collectors.toList());
            }

        }

    }

}
