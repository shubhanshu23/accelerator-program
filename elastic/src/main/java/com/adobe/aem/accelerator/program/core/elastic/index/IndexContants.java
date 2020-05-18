package com.adobe.aem.accelerator.program.core.elastic.index;

public class IndexContants {

    public static String DEFAULT_AEM = "{\n" +
            "  \"settings\":{\n" +
            "    \"analysis\":{\n" +
            "      \"analyzer\":{\n" +
            "        \"my_analyzer\":{\n" +
            "          \"type\":\"custom\",\n" +
            "          \"tokenizer\":\"standard\",\n" +
            "          \"filter\":[\n" +
            "            \"lowercase\"\n" +
            "          ]\n" +
            "        },\n" +
            "        \"my_stop_analyzer\":{\n" +
            "          \"type\":\"custom\",\n" +
            "          \"tokenizer\":\"standard\",\n" +
            "          \"filter\":[\n" +
            "            \"lowercase\",\n" +
            "            \"english_stop\"\n" +
            "          ]\n" +
            "        }\n" +
            "      },\n" +
            "      \"filter\":{\n" +
            "        \"english_stop\":{\n" +
            "          \"type\":\"stop\",\n" +
            "          \"stopwords\":\"_english_\"\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \"mappings\":{\n" +
            "    \"properties\":{\n" +
            "      \"type\": { \"type\": \"keyword\" },\n" +
            "      \"jcr:title\": {\n" +
            "        \"type\":\"text\",\n" +
            "        \"analyzer\":\"my_analyzer\",\n" +
            "        \"search_analyzer\":\"my_stop_analyzer\",\n" +
            "        \"search_quote_analyzer\":\"my_analyzer\"\n" +
            "      },\n" +
            "      \"jcr:lastModified\": {\n" +
            "        \"type\": \"date\"\n" +
            "      },\n" +
            "      \"cq:template\": {\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"path\": {\n" +
            "        \"type\": \"text\"\n" +
            "      },\n" +
            "      \"cq:lastModified\": {\n" +
            "        \"type\": \"date\"\n" +
            "      },\n" +
            "      \"jcr:description\": {\n" +
            "        \"type\":\"text\",\n" +
            "        \"analyzer\":\"my_analyzer\",\n" +
            "        \"search_analyzer\":\"my_stop_analyzer\",\n" +
            "        \"search_quote_analyzer\":\"my_analyzer\"\n" +
            "      },\n" +
            "      \"cq:tags\": {\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"dc:title\": {\n" +
            "        \"type\":\"text\",\n" +
            "        \"analyzer\":\"my_analyzer\",\n" +
            "        \"search_analyzer\":\"my_stop_analyzer\",\n" +
            "        \"search_quote_analyzer\":\"my_analyzer\"\n" +
            "      },\n" +
            "      \"dc:description\": {\n" +
            "        \"type\":\"text\",\n" +
            "        \"analyzer\":\"my_analyzer\",\n" +
            "        \"search_analyzer\":\"my_stop_analyzer\",\n" +
            "        \"search_quote_analyzer\":\"my_analyzer\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";
}
