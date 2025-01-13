package com.example.stm45;


import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

//to zmienic prawdopodobnie
@WebService(targetNamespace = "http://example.com/stm45")
public class MapService {

    @WebMethod
    public byte[] getFullMap(
            @WebParam(name = "topLeftX") int topLeftX,
            @WebParam(name = "topLeftY") int topLeftY,
            @WebParam(name = "bottomRightX") int bottomRightX,
            @WebParam(name = "bottomRightY") int bottomRightY
    ) throws IOException {

        //zmienic to
        String imagePath = "C:\\Users\\march3wa\\Desktop\\stm4-5\\src\\main\\resources\\map\\wroclaw.jpg";
//        String path = "../src/main/resources/map/wroclaw.jpg";

        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            throw new IOException("Map image not found at " + imagePath);
        }

        //obraz wczytany jako bufferedimage
        BufferedImage fullImage = ImageIO.read(imageFile);

        //obliczamy wysokosc i szerokosc wycinka
        int width = bottomRightX - topLeftX;
        int height = bottomRightY - topLeftY;

        //sprawdzamy, czy dane uzytkownika poprawne, czyli czy lewy górny punkt wiekszy niz 0 i czy prawy dolny mniejszy niz maksymalna wielkosc
        if (topLeftX < 0 || topLeftY < 0 || bottomRightX > fullImage.getWidth() || bottomRightY > fullImage.getHeight()) {
            throw new IllegalArgumentException("Coordinates are out of bounds");
        }

        //tu sprawdzamy czy wartosc X lub Y prawego punktu nie jest mniejsza niż lewego
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Invalid region dimensions");
        }

        //wycinamy fragment obrazu
        BufferedImage croppedImage = fullImage.getSubimage(topLeftX, topLeftY, width, height);

        //zapisujemy wyciety obraz i zwracamy w tablicy bajtow
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(croppedImage, "jpg", outputStream);

        return outputStream.toByteArray();
    }
}
