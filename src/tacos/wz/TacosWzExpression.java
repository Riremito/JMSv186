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
package tacos.wz;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class TacosWzExpression {

    public static int getInt(String val, int level) {
        // not an expression.
        if (val.matches("-?\\d+")) {
            return Integer.parseInt(val);
        }
        // expression for post BB Skill.wz. (JMS187+)
        ExpressionBuilder builder = new ExpressionBuilder(val);
        builder.variable("x"); // x : skill level.
        builder.variable("y"); // y : skill level? (KMS169)

        // thank you teto and chronos.
        Function ceil = new Function("u", 1) {
            @Override
            public double apply(double... doubles) {
                return Math.ceil(doubles[0]);
            }
        };

        Function floor = new Function("d", 1) {
            @Override
            public double apply(double... doubles) {
                return Math.floor(doubles[0]);
            }
        };

        // allowed, space, ()*+, -./0123456789, duxy
        if (!val.matches("[\\s\\(-\\+\\--9duxy]+")) {
            DebugLogger.ErrorLog("TacosWzExpression : " + val);
            System.exit(0);
        }

        builder.function(ceil); // u : 切り上げ
        builder.function(floor); // d : 切り捨て
        Expression expression = builder.build();

        expression.setVariable("x", level);
        expression.setVariable("y", level);

        int ret = (int) expression.evaluate();
        return ret;
    }

}
