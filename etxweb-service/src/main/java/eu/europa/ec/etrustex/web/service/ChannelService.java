package eu.europa.ec.etrustex.web.service;

import eu.europa.ec.etrustex.web.exchange.model.SearchItem;
import eu.europa.ec.etrustex.web.persistence.entity.exchange.Channel;
import eu.europa.ec.etrustex.web.service.validation.model.ChannelSpec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChannelService {
    Page<Channel> findByBusinessIdAndName(Long businessId, String name, Pageable pageable);

    Channel findById(Long id);

    List<Channel> findByBusinessIdAndEntityId(Long businessId, Long entityId);

    void delete(Long id);

    Channel create(ChannelSpec channelSpec);

    void update(Long channelId, ChannelSpec channelSpec);

    Channel findLastUpdatedChannel(String user);

    List<SearchItem> findByBusinessIdAndName(Long businessId, String name);
}
