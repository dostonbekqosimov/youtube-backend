package dasturlash.uz.service;

import dasturlash.uz.dto.request.ChannelMediaUpdateRequest;
import dasturlash.uz.dto.request.ChannelCreateRequest;
import dasturlash.uz.dto.response.ChannelResponseDTO;
import dasturlash.uz.entity.Channel;
import dasturlash.uz.enums.ChannelStatus;
import dasturlash.uz.enums.LanguageEnum;
import dasturlash.uz.exceptions.AppBadRequestException;
import dasturlash.uz.exceptions.ChannelExistsException;
import dasturlash.uz.exceptions.DataNotFoundException;
import dasturlash.uz.exceptions.SomethingWentWrongException;
import dasturlash.uz.repository.ChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static dasturlash.uz.security.SpringSecurityUtil.getCurrentUserId;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ResourceBundleService resourceBundleService;
    private final ChannelRepository channelRepository;

    public void create(ChannelCreateRequest request, LanguageEnum lang) {

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
            throw new SomethingWentWrongException(resourceBundleService.getMessage("something.went.wrong", lang));
        }


    }

    public void updateChannelInfo(String channelId, ChannelCreateRequest updateRequest, LanguageEnum lang) {


        Channel channel = getById(channelId);
        if (getCurrentUserId().equals(channel.getProfileId())) {
            try {
                if (!updateRequest.getName().equals(channel.getName())) {
                    channel.setName(updateRequest.getName());
                }
                if (!updateRequest.getDescription().equals(channel.getDescription())) {
                    channel.setDescription(updateRequest.getDescription());
                }
                if (!updateRequest.getHandle().equals(channel.getHandle())) {
                    existByHandle(updateRequest.getHandle());
                    channel.setHandle(updateRequest.getHandle());
                }
                channel.setUpdatedDate(LocalDateTime.now());
                channelRepository.save(channel);
            } catch (Exception e) {
                throw new SomethingWentWrongException(resourceBundleService.getMessage("something.went.wrong", lang));
            }
        } else {
            throw new AppBadRequestException("Nima yuborishni bilmadim, Hullas you can't change this channel info");
        }

    }

    public void updateChannelPhoto(String channelId, ChannelMediaUpdateRequest updateRequest, LanguageEnum lang) {
        Channel channel = getById(channelId);
        if (getCurrentUserId().equals(channel.getProfileId())) {
            try {
                if (updateRequest.getPhotoId() != null &&
                        !updateRequest.getPhotoId().equals(channel.getPhotoId())) {
                    channel.setPhotoId(updateRequest.getPhotoId());
                    channel.setUpdatedDate(LocalDateTime.now());
                    channelRepository.save(channel);
                }
            } catch (Exception e) {
                throw new SomethingWentWrongException(resourceBundleService.getMessage("something.went.wrong", lang));
            }
        } else {
            throw new AppBadRequestException("You can't change this channel photo");
        }
    }

    public void updateChannelBanner(String channelId, ChannelMediaUpdateRequest updateRequest, LanguageEnum lang) {
        Channel channel = getById(channelId);
        if (getCurrentUserId().equals(channel.getProfileId())) {
            try {
                if (updateRequest.getBannerId() != null &&
                        !updateRequest.getBannerId().equals(channel.getBannerId())) {
                    channel.setBannerId(updateRequest.getBannerId());
                    channel.setUpdatedDate(LocalDateTime.now());
                    channelRepository.save(channel);
                }
            } catch (Exception e) {
                throw new SomethingWentWrongException(resourceBundleService.getMessage("something.went.wrong", lang));
            }
        } else {
            throw new AppBadRequestException("You can't change this channel banner");
        }
    }

    private void existByHandle(String handle) {
        boolean isExist = channelRepository.existsByHandle(handle);
        if (isExist) {
            throw new ChannelExistsException("Channel with handle: " + handle + " exists");
        }
    }


    public ChannelResponseDTO getChannelById(String channelId) {

        // check if channel exists
        Channel channel = getById(channelId);

        return toChannelResponseDTO(channel);
    }

    private ChannelResponseDTO toChannelResponseDTO(Channel channel) {
        ChannelResponseDTO channelResponseDTO = new ChannelResponseDTO();
        channelResponseDTO.setId(channel.getId());
        channelResponseDTO.setName(channel.getName());
        channelResponseDTO.setDescription(channel.getDescription());
        channelResponseDTO.setHandle(channel.getHandle());
        channelResponseDTO.setProfileId(channel.getProfileId());
        channelResponseDTO.setStatus(channel.getStatus());
        channelResponseDTO.setCreatedDate(channel.getCreatedDate());
        channelResponseDTO.setUpdatedDate(channel.getUpdatedDate());
        channelResponseDTO.setVisible(channel.getVisible());
        channelResponseDTO.setBannerId(channel.getBannerId());
        channelResponseDTO.setPhotoId(channel.getPhotoId());

        return channelResponseDTO;
    }

    private Channel getById(String id) {
        return channelRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Channel not found"));
    }

    public List<ChannelResponseDTO> getChannelByName(String channelName) {

        List<Channel> channels = channelRepository.findByName((channelName));

        // check if there is channel by given name
        // Shu yerda agar hech qanday channel topilmasa videolardan nimadur berib yuborish kerak, yoki maslahatlashamiz(Doston)
        if (channels.isEmpty()) {
            throw new DataNotFoundException("No channel found");
        }

        return channels.stream().map(this::toChannelResponseDTO).toList();
    }

    public List<ChannelResponseDTO> getChannelByHandle(String channelHandle) {

        List<Channel> channels = channelRepository.findByHandle(channelHandle);

        // check if there is channel by given handle
        // Shu yerda agar hech qanday channel topilmasa videolardan nimadur berib yuborish kerak, yoki maslahatlashamiz(Doston)
        if (channels.isEmpty()) {
            throw new DataNotFoundException("No channel found");
        }

        return channels.stream().map(this::toChannelResponseDTO).toList();
    }


}