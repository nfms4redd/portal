/*
 *  Copyright (C) 2007-2012 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 *  GPLv3 + Classpath exception
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fao.unredd.testcommons;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * @author DamianoG
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TestRequirements {

    /**
     * Values that belong to the domain specified by TestConditions enum and represent the set of check to do before run a test
     * @return
     */
    TestConditions[] conditions() default TestConditions.NO_CONDITIONS;
}
