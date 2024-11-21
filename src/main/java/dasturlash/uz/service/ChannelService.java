package dasturlash.uz.service;

import dasturlash.uz.dto.request.ChannelCreateRequest;
import dasturlash.uz.entity.Channel;
import dasturlash.uz.enums.ChannelStatus;
import dasturlash.uz.exceptions.ChannelExistsException;
import dasturlash.uz.exceptions.SomethingWentWrongException;
import dasturlash.uz.repository.ChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

import java.time.LocalDateTime;

import static dasturlash.uz.security.SpringSecurityUtil.getCurrentUserId;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelRepository channelRepository;

    public void create(ChannelCreateRequest request) {

        // check if channel with handle exists
        existByHandle(request.getHandle());
        try {
            Channel newChannel = new Channel();
            newChannel.setName(request.getName());
            newChannel.setDescription(request.getDescription());
            newChannel.setHandle(request.getHandle());
            newChannel.setProfileId(getCurrentUserId());
            newChannel.setStatus(ChannelStatus.ACTIVE);
            newChannel.setCreatedDate(LocalDateTime.now());
            newChannel.setVisible(Boolean.TRUE);

            channelRepository.save(newChannel);
        } catch (Exception e) {
            throw new SomethingWentWrongException("Something went wrong");
        }


    }

    private void existByHandle(String handle) {
        boolean isExist = channelRepository.existsByHandle(handle);
        if (isExist) {
            throw new ChannelExistsException("Channel with handle: " + handle + " exists");
        }
    }
}
