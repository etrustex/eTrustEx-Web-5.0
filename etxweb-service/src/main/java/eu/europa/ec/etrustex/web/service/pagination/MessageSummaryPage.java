package eu.europa.ec.etrustex.web.service.pagination;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.JsonNode;
import eu.europa.ec.etrustex.web.common.exchange.view.InboxListViewFilter;
import eu.europa.ec.etrustex.web.persistence.entity.MessageSummary;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonView(InboxListViewFilter.class)
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class MessageSummaryPage extends RestResponsePage<MessageSummary> {
    private int unreadMessages;

    @Builder
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public MessageSummaryPage(@JsonProperty("content") List<MessageSummary> content,
                              @JsonProperty("number") int number,
                              @JsonProperty("size") int size,
                              @JsonProperty("totalElements") long totalElements,
                              @JsonProperty("pageable") JsonNode pageable,
                              @JsonProperty("last") boolean last,
                              @JsonProperty("totalPages") int totalPages,
                              @JsonProperty("sort") JsonNode sort,
                              @JsonProperty("first") boolean first,
                              @JsonProperty("numberOfElements") int numberOfElements,
                              @JsonProperty("unreadMessages") int unreadMessages) {

        this(content, PageRequest.of(number, size), totalElements, unreadMessages);
    }

    public MessageSummaryPage(List<MessageSummary> content, Pageable pageable, long total, int unreadMessages) {
        super(content, pageable, total);
        this.unreadMessages = unreadMessages;
    }
}
