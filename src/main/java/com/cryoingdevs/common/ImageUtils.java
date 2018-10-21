package com.cryoingdevs.common;

import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by IvànAlejandro on 21/10/2018.
 */
public class ImageUtils {
    public static void decodeImage64base() {
        String value = "Wyk+HjAxHTAyNzg3MDUdODQwHTAxOR0wMDAwMDAwMDAwMDAwMDAd" +
                "RkRFQh0wMDAwMDAwHTA0MB0dMS8xHTUwLjVMQh1OHVcgMzR0aCBTdHJlZXQdQ" +
                "XVzdGluHVRYHSAeMDYdMTBaR0QwMDQdMTFaUmVjaXBpZW50IENvbXBhbnkgTmFt" +
                "ZR0xMlo5MDEyNjM3OTA2HTE0WioqVEVTVCBMQUJFTCAtIERPIE5PVCBTSElQKio" +
                "dMjNaTh0yMlocWR0yMFogHDAdMjZaNjEzMxwdHgQ=";
    }
}
