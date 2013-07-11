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

import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

/**
 * Classes that inherit from this perform a check for one or more condition that must be satisfied, otherwise the test is skipped without failures.
 * 
 * @author DamianoG
 * 
 */
public abstract class RequirementsChecker {

    private static final Logger LOGGER = Logger.getLogger(RequirementsChecker.class);

    @Rule
    public TestName testName = new TestName();

    /**
     * Scan for the annotation TestRequirements, if it is found performs the checks specified in annotation parameters
     */
    @Before
    public void setup() {
        try {
            String test = testName.getMethodName();
            TestRequirements annotation = this.getClass().getDeclaredMethod(test, new Class[0])
                    .getAnnotation(TestRequirements.class);
            if (annotation == null){
                return;
            }
            TestConditions[] conditions = annotation.conditions();
            for (TestConditions condition : conditions) {
                // TODO remove this awful switch and use something that must not be changed when a new check must be added 
                // (f.e. search the method to launch using reflection and naming convenctions)
                switch (condition) {
                case GDAL:
                    performGDALChecks();
                    break;
                case UNIX:
                    performUNIXChecks();
                    break;
                default:
                    break;
                }
            }
        } catch (SecurityException e1) {
            LOGGER.error(e1.getMessage(), e1);
        } catch (NoSuchMethodException e1) {
            LOGGER.error(e1.getMessage(), e1);
        }
    }

    /**
     * Some Tests need GDAL to work.
     * Check if GDAL is avaiable in the system .
     */
    private void performUNIXChecks() {
        if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
            LOGGER.warn(testName.getMethodName()
                    + " :: this test can't run on windows, skipping...");
            Assume.assumeTrue(false);
        }
    }

    /**
     * Some tests run just on unix system
     * Check if the SO is Unix
     */
    private void performGDALChecks() {
        try {
            Runtime run = Runtime.getRuntime();
            run.exec("gdalinfo");
        } catch (IOException e) {
            LOGGER.warn(testName.getMethodName()
                    + " :: this test can't run without GDAL avaiable, skipping...");
            Assume.assumeTrue(false);
        }
    }
}
