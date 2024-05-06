package cn.chuanwise.xiaoming.resource;

import cn.chuanwise.toolkit.preservable.AbstractPreservable;
import cn.chuanwise.xiaoming.bot.XiaoMingBot;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.SingleMessage;
import net.mamoe.mirai.utils.ExternalResource;
import org.slf4j.Logger;

import java.beans.Transient;
import java.io.*;
import java.net.URL;
import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ResourceManagerImpl extends AbstractPreservable implements ResourceManager {
    transient File imagesDirectory;
    transient File resourceDirectory;
    @Setter
    transient XiaoMingBot xiaoMingBot;
    Map<String, Long> imageLastVisitTimes = new HashMap<>();

    @Override
    @Transient
    public File getImagesDirectory() {
        return imagesDirectory;
    }

    @Transient
    public File getResourceDirectory() {
        return resourceDirectory;
    }

    @Override
    public void setResourceDirectory(File resourceDirectory) {
        this.resourceDirectory = resourceDirectory;
        imagesDirectory = new File(resourceDirectory, "images");
        imagesDirectory.mkdirs();
    }

    @Transient
    @Override
    public Logger getLogger() {
        return log;
    }

    @Override
    public MessageChain useResources(MessageChain messages, Contact miraiContact) {
        List<SingleMessage> resultMessages = new ArrayList<>(messages.size());
        messages.forEach(singleMessage -> {
            if (singleMessage instanceof Image) {
                resultMessages.add(getImage(((Image) singleMessage).getImageId(), miraiContact));
            } else {
                resultMessages.add(singleMessage);
            }
        });
        final MessageChainBuilder builder = new MessageChainBuilder(resultMessages.size());
        builder.addAll(resultMessages);
        return builder.asMessageChain();
    }

    @Override
    public Image getImage(String id, Contact miraiContact) {
        final File image = getImage(id);
        if (Objects.nonNull(image)) {
            return ExternalResource.uploadAsImage(image, miraiContact);
        } else {
            return Image.fromId(id);
        }
    }

    @Override
    public File saveImage(Image image) throws IOException {
        URL url = new URL(Image.queryUrl(image));
        final InputStream inputStream = url.openConnection().getInputStream();

        final File imageFile = new File(imagesDirectory, image.getImageId());
        if (!imageFile.isFile()) {
            imageFile.createNewFile();
        }

        int packSize = 1024;
        byte[] bytes = new byte[packSize];
        int len = 0;
        try (OutputStream outputStream = new FileOutputStream(imageFile)) {
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }
        }
        inputStream.close();

        imageLastVisitTimes.put(image.getImageId(), System.currentTimeMillis());
        getXiaoMingBot().getFileSaver().readyToSave(this);
        return imageFile;
    }

    @Override
    public File getImage(String id) {
        final File file = new File(imagesDirectory, id);
        if (file.exists()) {
            imageLastVisitTimes.put(id, System.currentTimeMillis());
            getXiaoMingBot().getFileSaver().readyToSave(this);
            return file;
        } else {
            return null;
        }
    }
}