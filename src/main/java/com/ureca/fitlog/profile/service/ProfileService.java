package com.ureca.fitlog.profile.service;

import com.ureca.fitlog.common.exception.BusinessException;
import com.ureca.fitlog.common.exception.ExceptionStatus;
import com.ureca.fitlog.profile.dto.request.ProfileRequestDTO;
import com.ureca.fitlog.profile.dto.response.ProfileResponseDTO;
import com.ureca.fitlog.profile.mapper.ProfileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

    private final ProfileMapper profileMapper;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_CONTENT_TYPES = {
            "image/jpeg", "image/png", "image/jpg", "image/webp"
    };
    private static final int MAX_WIDTH = 300;
    private static final int MAX_HEIGHT = 300;

    /** 로그인 아이디로 회원 프로필 조회 + 나이 계산 */
    public ProfileResponseDTO getProfileByLoginId(String loginId) {
        if (loginId == null || loginId.isBlank()) {
            throw new BusinessException(ExceptionStatus.TODO_AUTH_LOGIN_INFO_NOT_FOUND);
        }

        ProfileResponseDTO profile = profileMapper.findProfileByLoginId(loginId);
        if (profile == null) {
            throw new BusinessException(ExceptionStatus.USER_DOMAIN_NOT_FOUND);
        }
        profile.calculateAgeFromBirthDate();
        return profile;
    }

    /** 이미지 리사이징 */
    private BufferedImage resizeImage(BufferedImage originalImage, int maxWidth, int maxHeight) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        double ratio = Math.min(
                (double) maxWidth / originalWidth,
                (double) maxHeight / originalHeight
        );

        int newWidth = (int) (originalWidth * ratio);
        int newHeight = (int) (originalHeight * ratio);

        Image scaledImage = originalImage.getScaledInstance(
                newWidth,
                newHeight,
                Image.SCALE_SMOOTH
        );

        BufferedImage resizedImage = new BufferedImage(
                newWidth,
                newHeight,
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();

        return resizedImage;
    }

    /** 이미지 파일을 리사이징 후 Base64로 변환 */
    private String convertImageToBase64(MultipartFile file) {
        try {
            // 파일 사이즈가 5mb 이상이면 에러
            if (file.getSize() > MAX_FILE_SIZE) {
                throw new BusinessException(ExceptionStatus.FILE_SIZE_EXCEEDED);
            }

            // MIME 타입 검증 (Content-Type만 허용)
            String contentType = file.getContentType();
            boolean isValidType = false;
            for (String allowedType : ALLOWED_CONTENT_TYPES) {
                if (allowedType.equals(contentType)) {
                    isValidType = true;
                    break;
                }
            }
            if (!isValidType) {
                throw new BusinessException(ExceptionStatus.INVALID_FILE_TYPE);
            }

            // 실제 이미지인지 검증
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            // 이미지 크기 초과 시 리사이징
            if (originalImage == null) {
                throw new BusinessException(ExceptionStatus.INVALID_FILE_TYPE);
            }

            BufferedImage processedImage = originalImage;
            if (originalImage.getWidth() > MAX_WIDTH || originalImage.getHeight() > MAX_HEIGHT) {
                processedImage = resizeImage(originalImage, MAX_WIDTH, MAX_HEIGHT);
                log.info("이미지 리사이징: {}x{} -> {}x{}",
                        originalImage.getWidth(), originalImage.getHeight(),
                        processedImage.getWidth(), processedImage.getHeight());
            }

            ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream();
            ImageIO.write(processedImage, "jpg", imageOutputStream);
            byte[] bytes = imageOutputStream.toByteArray();

            log.info("원본 파일 크기: {}KB, 압축 후: {}KB",
                    file.getSize() / 1024, bytes.length / 1024);

            // Base64 변환
            String base64 = Base64.getEncoder().encodeToString(bytes);
            return "data:image/jpeg;base64," + base64;
        } catch (IOException e) {
            log.error("이미지 변환 실패", e);
            throw new BusinessException(ExceptionStatus.FILE_CONVERSION_FAILED);
        }
    }

    /** 프로필 수정 */
    @Transactional
    public ProfileResponseDTO updateProfile(
            String loginId,
            ProfileRequestDTO request,
            MultipartFile profileImage
    ) {
        if (loginId == null || loginId.isBlank()) {
            throw new BusinessException(ExceptionStatus.TODO_AUTH_LOGIN_INFO_NOT_FOUND);
        }

        // 이미지 업데이트 여부 판단
        String base64Image = null;
        boolean shouldUpdateImage = false;

        if (profileImage != null && !profileImage.isEmpty()) {
            // 새 이미지 업로드
            base64Image = convertImageToBase64(profileImage);
            shouldUpdateImage = true;
        } else if (profileImage == null) {
            // 명시적으로 null이 전달되면 이미지 삭제
            base64Image = null;
            shouldUpdateImage = true;
        }

        int updated = profileMapper.updateProfile(loginId, request, base64Image, shouldUpdateImage);

        if (updated == 0) {
            throw new BusinessException(ExceptionStatus.USER_DOMAIN_NOT_FOUND);
        }
        return getProfileByLoginId(loginId);
    }
}