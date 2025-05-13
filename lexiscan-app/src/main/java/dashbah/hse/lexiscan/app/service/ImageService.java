package dashbah.hse.lexiscan.app.service;

import dashbah.hse.lexiscan.app.exception.ImageNotFoundException;

public interface ImageService {
    void saveImage(String key, byte[] image);

    byte[] getImageBinaryDataByUid(String imageUid) throws ImageNotFoundException;
}
