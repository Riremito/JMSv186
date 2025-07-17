/*
 * Copyright (C) 2025 Riremito
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */
package config;

import java.nio.charset.Charset;

/**
 *
 * @author Riremito
 */
public class CodePage {

    private static Charset codepage = null;

    public static Charset getCodePage() {
        return codepage;
    }

    public static void init() {
        if (DeveloperMode.DM_CODEPAGE_UTF8.get()) {
            codepage = Charset.forName("UTF8");
            return;
        }

        switch (Region.getRegion()) {
            case KMS:
            case KMST: {
                codepage = Charset.forName("MS949"); // korean
                return;
            }
            case JMS:
            case JMST: {
                codepage = Charset.forName("MS932"); // Shift-JIS
                return;
            }
            case CMS: {
                codepage = Charset.forName("MS936"); // GBK
                return;
            }
            case TWMS: {
                codepage = Charset.forName("MS950"); // big5
                return;
            }
            case THMS: {
                codepage = Charset.forName("MS874");
                return;
            }
            default: {
                break;
            }
        }

        codepage = Charset.forName("MS932"); // Shift-JIS default
        return;
    }
}
