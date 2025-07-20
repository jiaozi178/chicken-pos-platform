package fun.cyhgraph.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AvatarFileUtil {
    /**
     * 保存Base64头像图片到文件系统
     */
    public static String saveBase64Avatar(String base64Data) {
        try {
            String[] dataParts = base64Data.split(",");
            String imageData = dataParts[1];
            String mimeType = dataParts[0].split(";")[0].split(":")[1];
            String extension = mimeType.split("/")[1];
            String fileName = java.util.UUID.randomUUID().toString() + "." + extension;

            String uploadDir = "upload" + File.separator + "avatars" + File.separator;
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            byte[] imageBytes = java.util.Base64.getDecoder().decode(imageData);
            String filePath = uploadDir + fileName;
            Files.write(Paths.get(filePath), imageBytes);

            return "/upload/avatars/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("头像图片保存失败");
        }
    }
}
