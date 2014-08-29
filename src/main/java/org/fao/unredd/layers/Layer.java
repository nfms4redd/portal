/**
 * nfms4redd Portal Interface - http://nfms4redd.org/
 *
 * (C) 2012, FAO Forestry Department (http://www.fao.org/forestry/)
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation;
 * version 3.0 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */
package org.fao.unredd.layers;

import java.io.IOException;

/**
 * Represents the configuration for a layer.
 * 
 * @author fergonco
 */
public interface Layer {

    /**
     * Get a list of all the output identifiers in this layer
     * 
     * @return
     * @throws IOException
     */
    Outputs getOutputs() throws IOException;

    /**
     * Get the output content with the specified id
     * 
     * @param outputId
     * @return
     * @throws NoSuchIndicatorException
     */
    String getOutput(String outputId) throws NoSuchIndicatorException,
            IOException;

}
