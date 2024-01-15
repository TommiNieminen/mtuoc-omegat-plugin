/*
 *  OmegaT - Computer Assisted Translation (CAT) tool
 *           with fuzzy matching, translation memory, keyword search,
 *           glossaries, and translation leveraging into updated projects.
 *
 *  Copyright (C) 2012 Alex Buloichik, Didier Briel
 *                2016-2017 Aaron Madlon-Kay
 *                2018 Didier Briel
 *                2022,2023 Hiroshi Miura
 *                Home page: https://www.omegat.org/
 *                Support center: https://omegat.org/support
 *
 *  This file is part of OmegaT.
 *
 *  OmegaT is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  OmegaT is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.omegat.connectors.machinetranslators.mtuoc;

import org.omegat.util.HttpConnectionUtils;

import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Support for Microsoft Translator API machine translation.
 * @author Hiroshi Miura
 */
public class MtuocTranslator extends MtuocTranslatorBase {



    private String urlTranslate;
    private final ObjectMapper mapper = new ObjectMapper();

    public MtuocTranslator(MtuocPlugin parent, String translateEndpointUrl) {
        super(parent);
        urlTranslate = translateEndpointUrl;
    }

    @Override
    protected String requestTranslate(String langFrom, String langTo, String text) throws Exception {
        Map<String, String> p = new TreeMap<>();
        //Modify this if API key support is need
        //p.put("Ocp-Apim-Subscription-Key", parent.getKey());
        String url = urlTranslate;
        String json = createJsonRequest(text);
        String res = HttpConnectionUtils.postJSON(url, json, p);
        JsonNode root = mapper.readValue(res,JsonNode.class);
        JsonNode translation = root.get("tgt");

        if (translation == null) {
            return null;
        }
        return translation.asText();
    }

    /**
     * Method for test.
     * @param url alternative url.
     */
    public void setUrl(String url) {
        urlTranslate = url;
    }

    /**
     * Create request and return as json string.
     */
    protected String createJsonRequest(String trText) throws JsonProcessingException {
        Map<String, Object> param = new TreeMap<>();
        param.put("src", trText);
        param.put("id", java.util.UUID.randomUUID());
        return new ObjectMapper().writeValueAsString(param);
    }
}
