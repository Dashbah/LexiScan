package dashbah.hse.lexiscan.app.service;

import dashbah.hse.lexiscan.app.entity.Image;
import dashbah.hse.lexiscan.app.exception.ImageNotFoundException;

public interface ImageService {
    public void saveImage(Image image);

    public byte[] getImageBinaryDataByUid(String imageUid) throws ImageNotFoundException;
}
