/*
 * Copyright (C) 2026 Riremito
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
package tacos;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import tacos.config.Region;
import tacos.config.Version;

/**
 *
 * @author Riremito
 */
public class VersionSelector {

    private static String[] versions
            = {
                // developing
                "JMS v147.0",
                "JMS v164.0",
                "JMS v186.1",
                "JMS v194.0",
                "JMS v302.0",
                "KMST v330.1",
                "GMS v95.1",
                "GMS v83.1",
                // all list.
                "KMS v1.1",
                "KMS v31.1",
                "KMS v41.1",
                "KMS v43.1",
                "KMS v55.1",
                "KMS v65.1",
                //"KMS v71.1",
                "KMS v84.1",
                "KMS v92.2",
                "KMS v95.1",
                "KMS v114.1",
                "KMS v118.1",
                "KMS v119.1",
                "KMS v121.1",
                "KMS v127.1",
                "KMS v138.1",
                "KMS v148.1",
                "KMS v149.1",
                //"KMS v150.1",
                //"KMS v151.1",
                "KMS v160.1",
                "KMS v169.2",
                //"KMS v174.2",
                //"KMS v183.1",
                "KMS v197.2",
                //"KMS v200.1",
                "JMS v131.0",
                "JMS v146.0",
                "JMS v147.0",
                "JMS v164.0",
                "JMS v165.0",
                "JMS v180.1",
                "JMS v186.1",
                "JMS v187.0",
                "JMS v188.0",
                "JMS v194.0",
                "JMS v302.0",
                "JMS v308.0",
                "KMSB v268.1", // 1.68
                "KMST v330.1",
                "KMST v391.1",
                "JMST v110.0",
                "CMS v85.1",
                "CMS v104.1",
                //"TWMS v74.1",
                "TWMS v77.1",
                "TWMS v96.1",
                "TWMS v121.1",
                //"TWMS v122.1",
                //"TWMS v124.1",
                "TWMS v125.1",
                "TWMS v148.1",
                "MSEA v100.1",
                "MSEA v102.1",
                //"GMS v61.1",
                "GMS v62.1",
                "GMS v65.1",
                "GMS v66.1",
                "GMS v68.1",
                "GMS v72.1",
                "GMS v73.1",
                "GMS v83.1",
                "GMS v84.1",
                "GMS v91.1",
                //"GMS v92.1",
                "GMS v95.1",
                "GMS v111.1",
                "GMS v116.1",
                "GMS v117.1",
                "GMS v126.1",
                "GMS v131.1",
                "EMS v55.1",
                "EMS v70.1",
                "EMS v76.2",
                "EMS v89.2",
                "THMS v87.0",
                //"THMS v88.0",
                "THMS v96.0",
                "BMS v24.0",
                "VMS v35.0",
                "IMS v1.1"
            };

    public static boolean open() {
        JFrame frame = new JFrame();
        JDialog dialog = new JDialog(frame, "Kaede Server - Version Select", true);
        dialog.setLocationRelativeTo(null);
        dialog.setSize(400, 300);
        dialog.setLayout(new FlowLayout());

        JComboBox<String> comboBox = new JComboBox<>(versions);

        comboBox.addActionListener(e -> {
            String selected = (String) comboBox.getSelectedItem();
            Pattern pattern = Pattern.compile("([A-Z]{3,}) v([0-9]{1,})\\.([0-9])");
            Matcher matcher = pattern.matcher(selected);
            if (matcher.matches()) {
                String server_region = matcher.group(1);
                int server_version = Integer.parseInt(matcher.group(2));
                int server_version_sub = Integer.parseInt(matcher.group(3));
                Region.setRegion(server_region);
                Version.setVersion(server_version, server_version_sub);
            }
            dialog.dispose();
        });

        comboBox.setPreferredSize(new Dimension(200, 60));
        dialog.add(comboBox);
        dialog.setVisible(true);
        return true;
    }
}
