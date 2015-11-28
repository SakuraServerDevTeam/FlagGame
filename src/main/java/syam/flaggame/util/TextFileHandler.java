/* 
 * Copyright (C) 2015 Syamn, SakuraServerDev
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
package syam.flaggame.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class TextFileHandler {
    private final String p;

    public TextFileHandler(String path) {
        p = path;
        if (!new File(p).exists()) {
            try {
                // 無ければ作る
                new File(p).createNewFile();
            } catch (IOException ex) {
            }
        }
    }

    /**
     * ファイルを行ごとに全取得
     * 
     * @return 行ごとの{@code List<String>}
     * @throws FileNotFoundException
     * @throws IOException
     */
    public List<String> readLines() throws FileNotFoundException, IOException {
        BufferedReader inputStream = null;
        List<String> data = new ArrayList<>();
        try {
            inputStream = new BufferedReader(new FileReader(p));
            String l;

            while ((l = inputStream.readLine()) != null) {
                data.add(l);
            }
            /* 最終処理 */
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return data;
    }

    /**
     * ファイルを書き込み
     * 
     * @param data
     * @throws IOException
     */
    public void writeLines(List<String> data) throws IOException {
        try (PrintWriter outputStream = new PrintWriter(new FileWriter(p))) {
            while (!data.isEmpty()) {
                outputStream.println(data.remove(0));
            }
        }
    }

    /**
     * ファイルの最終行に追記
     * 
     * @param line
     * @throws IOException
     */
    public void appendLine(String line) throws IOException {
        try (PrintWriter outputStream = new PrintWriter(new FileWriter(p, true))) {
            outputStream.println(line);
        }
    }

}
