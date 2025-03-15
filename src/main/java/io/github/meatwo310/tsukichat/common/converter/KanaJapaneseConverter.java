package io.github.meatwo310.tsukichat.common.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class KanaJapaneseConverter {
    public static final Logger LOGGER = LoggerFactory.getLogger(KanaJapaneseConverter.class);
    private static final String API_URL = "https://www.google.com/transliterate";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private KanaJapaneseConverter() {
    }

    public static String convertToJapanese(String hiragana) {
        if (hiragana == null || hiragana.isEmpty()) {
            return hiragana;
        }

        try {
            // Google CGI API for Japanese Inputを呼び出す
            URI uri = new URIBuilder(API_URL)
                    .addParameter("langpair", "ja-Hira|ja")
                    .addParameter("text", hiragana)
                    .build();

            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpGet request = new HttpGet(uri);
                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    String jsonResponse = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

                    // レスポンスをパース
                    JsonNode root = MAPPER.readTree(jsonResponse);
                    StringBuilder result = new StringBuilder();

                    for (JsonNode segment : root) {
                        if (segment.isArray() && segment.size() >= 2) {
                            JsonNode candidatesNode = segment.get(1);
                            if (candidatesNode.isArray() && !candidatesNode.isEmpty()) {
                                // 最初の候補を選択
                                result.append(candidatesNode.get(0).asText());
                            }
                        }
                    }

                    return result.toString();
                }
            }
        } catch (URISyntaxException | IOException e) {
            LOGGER.error("Failed to convert hiragana to kanji", e);
            // 変換に失敗した場合は元の文字列を返す
            return hiragana;
        }
    }
}