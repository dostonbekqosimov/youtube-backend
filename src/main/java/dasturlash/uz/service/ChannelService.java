package dasturlash.uz.service;

import dasturlash.uz.dto.request.channel.ChannelMediaUpdateRequest;
import dasturlash.uz.dto.request.channel.ChannelCreateRequest;
import dasturlash.uz.dto.request.UpdateChannelStatusRequest;
import dasturlash.uz.dto.response.MediaUrlDTO;
import dasturlash.uz.dto.response.channel.ChannelResponseDTO;
import dasturlash.uz.dto.response.channel.VideoChannelDTO;
import dasturlash.uz.entity.Channel;
import dasturlash.uz.enums.ChannelStatus;
import dasturlash.uz.enums.LanguageEnum;
import dasturlash.uz.enums.ProfileRole;
import dasturlash.uz.exceptions.AppBadRequestException;
import dasturlash.uz.exceptions.ChannelExistsException;
import dasturlash.uz.exceptions.DataNotFoundException;
import dasturlash.uz.exceptions.SomethingWentWrongException;
import dasturlash.uz.mapper.ChannelShortInfoMapper;
import dasturlash.uz.repository.ChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static dasturlash.uz.security.SpringSecurityUtil.getCurrentUserId;
import static dasturlash.uz.security.SpringSecurityUtil.getCurrentUserRole;

@Service
@RequiredArgsConstructor
public class ChannelService {


    private final ResourceBundleService resourceBundleService;
    private final ChannelRepository channelRepository;
    private final AttachService attachService;

    @Value("${app.domain}")
    private String domain;

    public String create(ChannelCreateRequest request, LanguageEnum lang) {

        // check if channel with handle exists
        existByHandle(request.getHandle());
        try {
            Channel newChannel = new Channel();
            newChannel.setName(request.getName());
            newChannel.setDescription(request.getDescription());
            newChannel.setHandle("@" + request.getHandle());
            newChannel.setProfileId(getCurrentUserId());
            newChannel.setStatus(ChannelStatus.ACTIVE);
            newChannel.setCreatedDate(LocalDateTime.now());
            newChannel.setVisible(Boolean.TRUE);

            channelRepository.save(newChannel);
            return newChannel.getId();
        } catch (Exception e) {
            throw new SomethingWentWrongException(resourceBundleService.getMessage("something.went.wrong", lang));
        }


    }

    public String updateChannelInfo(String channelId, ChannelCreateRequest updateRequest, LanguageEnum lang) {


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
                return channel.getId();
            } catch (Exception e) {
                throw new SomethingWentWrongException(resourceBundleService.getMessage("something.went.wrong", lang));
            }
        } else {
            throw new AppBadRequestException("Nima yuborishni bilmadim, Hullas you can't change this channel info");
        }

    }

    public String updateChannelPhoto(String channelId, ChannelMediaUpdateRequest updateRequest, LanguageEnum lang) {
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
        return channel.getId();
    }

    public String updateChannelBanner(String channelId, ChannelMediaUpdateRequest updateRequest, LanguageEnum lang) {
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
        return channel.getId();

    }

    public String updateChannelStatus(UpdateChannelStatusRequest request, LanguageEnum lang) {
        Channel channel = getById(request.getChannelId()); // Fetch channel by ID

        ProfileRole userRole = getCurrentUserRole(); // Fetch current user's role
        Long currentUserId = getCurrentUserId();   // Fetch current user's ID

        // Handle based on roles
        if (userRole == ProfileRole.ROLE_ADMIN) {
            // Admins can update any channel status
            changeChannelStatus(request, channel);
        } else if (userRole == ProfileRole.ROLE_USER) {
            // Owners can only update their own channels
            if (!currentUserId.equals(channel.getProfileId())) {
                throw new AppBadRequestException(
                        resourceBundleService.getMessage("error.not.owner.of.channel", lang)
                );
            }
            changeChannelStatus(request, channel);
        } else {
            // Other roles (if any) are unauthorized
            throw new AppBadRequestException(
                    resourceBundleService.getMessage("something.went.wrong", lang)
            );
        }
        return channel.getId();

    }

    private void changeChannelStatus(UpdateChannelStatusRequest request, Channel channel) {
        if (!request.getStatus().equals(channel.getStatus())) {
            channel.setStatus(request.getStatus());
            channel.setUpdatedDate(LocalDateTime.now());
            channelRepository.save(channel);
        }
    }

    public ChannelResponseDTO getChannelById(String channelId) {

        // check if channel exists
        Channel channel = getById(channelId);

        return toChannelResponseDTO(channel);
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

    public ChannelResponseDTO getChannelByHandle(String channelHandle) {

        Channel channel = channelRepository.findByHandle(channelHandle);

        // check if there is channel by given handle
        // Shu yerda agar hech qanday channel topilmasa videolardan nimadur berib yuborish kerak, yoki maslahatlashamiz(Doston)
        if (channel == null) {
            throw new DataNotFoundException("No channel found");
        }

        return toChannelResponseDTO(channel);
    }

    public PageImpl<ChannelResponseDTO> getChannelsList(int page, Integer size) {

        Pageable pageRequest = PageRequest.of(page, size);

        Page<Channel> channelPage = channelRepository.findAllByVisibleTrue(pageRequest);

        if (channelPage.isEmpty()) {
            throw new DataNotFoundException("No channel found");
        }

        // Convert to DTOs
        List<ChannelResponseDTO> responseDTOS = channelPage
                .stream()
                .map(this::toChannelResponseDTO)
                .toList();
        // Create a new Page with the DTOs
        return new PageImpl<>(responseDTOS, pageRequest, channelPage.getTotalElements());
    }

    public PageImpl<ChannelResponseDTO> getUserChannelList(int page, Integer size) {

        Pageable pageRequest = PageRequest.of(page, size);

        Long currentUserId = getCurrentUserId();

        Page<Channel> channelPage = channelRepository.findAllByProfileIdAndVisibleTrue(currentUserId, pageRequest);

        if (channelPage.isEmpty()) {
            throw new DataNotFoundException("No channel found");
        }

        // Convert to DTOs
        List<ChannelResponseDTO> responseDTOS = channelPage
                .stream()
                .map(this::toChannelResponseDTO)
                .toList();
        // Create a new Page with the DTOs
        return new PageImpl<>(responseDTOS, pageRequest, channelPage.getTotalElements());
    }

    private void existByHandle(String handle) {
        boolean isExist = channelRepository.existsByHandle(handle);
        if (isExist) {
            throw new ChannelExistsException("Channel with handle: " + handle + " exists");
        }
    }

    public Channel getById(String id) {
        return channelRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Channel not found"));
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

        // get media urls
        MediaUrlDTO banner = attachService.getUrlOfMedia(channel.getPhotoId());
        MediaUrlDTO photo = attachService.getUrlOfMedia(channel.getBannerId());


        // set media urls
        channelResponseDTO.setBanner(banner);
        channelResponseDTO.setPhoto(photo);


        return channelResponseDTO;
    }

    // shuni optimal qilish kerak
    // keyin handle qo'shayotganda @ qo'shimchasi bilan qo'shish kerek
    public String shareChannel(String channelId) {


        Channel channel = getById(channelId);

        return makeShareLink(channel.getHandle());
    }

    public VideoChannelDTO getVideoChannelShortInfo(String channelId) {
        ChannelShortInfoMapper result = channelRepository.getShortInfo(channelId);
        VideoChannelDTO videoChannelDTO = new VideoChannelDTO();
        videoChannelDTO.setId(result.getId());
        videoChannelDTO.setName(result.getName());
        videoChannelDTO.setPhotoUrl(attachService.openURL(result.getPhotoId()));
        return videoChannelDTO;
    }


    private String makeShareLink(String handle) {
        return domain + "/api/channels/handle/" + handle;
    }

    public Long getChannelOwnerId(String channelId) {
        if (channelId == null) {
            return null;
        }
        return channelRepository.getChannelOwnerId(channelId);
    }

    public List<String> getChannelOwnerIds(Long profileId) {
        return channelRepository.getChannelIdsByProfileId(profileId);
    }
}
