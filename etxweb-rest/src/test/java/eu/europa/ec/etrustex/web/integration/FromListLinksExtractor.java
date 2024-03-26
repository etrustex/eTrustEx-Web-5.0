package eu.europa.ec.etrustex.web.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.restdocs.hypermedia.Link;
import org.springframework.restdocs.hypermedia.LinkExtractor;
import org.springframework.restdocs.operation.OperationResponse;

import java.io.IOException;
import java.util.*;

/*
 * implementation adapted from protected class org.springframework.restdocs.hypermedia.HalLinkExtractor
 * */
@SuppressWarnings("unchecked")
public class FromListLinksExtractor implements LinkExtractor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, List<Link>> extractLinks(OperationResponse response)
            throws IOException {
        List<Map<String, Object>> jsonContent = this.objectMapper
                .readValue(response.getContent(), List.class);
        return extractLinks(jsonContent.get(0));
    }

    private Map<String, List<Link>> extractLinks(Map<String, Object> json) {
        Map<String, List<Link>> extractedLinks = new LinkedHashMap<>();
        Object possibleLinks = json.get("links");
        if (possibleLinks instanceof List) {

            List<Object> linksList = (List<Object>) possibleLinks;

            for(Object object: linksList) {
                if(object instanceof Map && ((Map<String, Object>) object).containsKey("rel")) {
                    String rel = ((Map<String, Object>) object).get("rel").toString();
                    extractedLinks.put(rel, convertToLinks(object, rel));
                }
            }
        }
        return extractedLinks;
    }

    private static List<Link> convertToLinks(Object object, String rel) {
        List<Link> links = new ArrayList<>();
        if (object instanceof Collection) {

            Collection<Object> possibleLinkObjects = (Collection<Object>) object;
            for (Object possibleLinkObject : possibleLinkObjects) {
                maybeAddLink(maybeCreateLink(rel, possibleLinkObject), links);
            }
        }
        else {
            maybeAddLink(maybeCreateLink(rel, object), links);
        }
        return links;
    }

    private static Link maybeCreateLink(String rel, Object possibleLinkObject) {
        if (possibleLinkObject instanceof Map) {
            Map<?, ?> possibleLinkMap = (Map<?, ?>) possibleLinkObject;
            Object hrefObject = possibleLinkMap.get("href");
            if (hrefObject instanceof String) {
                Object titleObject = possibleLinkMap.get("title");
                return new Link(rel, (String) hrefObject,
                        (titleObject instanceof String) ? (String) titleObject : null);
            }
        }
        return null;
    }

    private static void maybeAddLink(Link possibleLink, List<Link> links) {
        if (possibleLink != null) {
            links.add(possibleLink);
        }
    }
}
