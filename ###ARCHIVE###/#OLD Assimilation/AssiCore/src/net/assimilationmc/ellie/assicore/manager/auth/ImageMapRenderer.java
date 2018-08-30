package net.assimilationmc.ellie.assicore.manager.auth;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.*;

/**
 * Created by Ellie on 02/09/2017 for AssimilationMC.
 * <p>
 * Copyright 2017 Ellie
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class ImageMapRenderer extends MapRenderer {

    private static final byte black = MapPalette.matchColor(Color.BLACK);
    private static final String encodeFormat = "otpauth://totp/%s@%s?secret=%s";
    private final BitMatrix bitMatrix;

    public ImageMapRenderer(String username, String secret, String serverip) throws WriterException {
        this.bitMatrix = getQRMap(username, secret, serverip);
    }

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
        for (int x = 0; x < 128; x++) {
            for (int z = 0; z < 128; z++) {
                mapCanvas.setPixel(x, z, bitMatrix.get(x, z) ? black : MapPalette.WHITE);
            }
        }
    }

    private BitMatrix getQRMap(String username, String secret, String serverIp) throws WriterException {
        return new QRCodeWriter().encode(String.format(encodeFormat,
                username,
                serverIp,
                secret),
                BarcodeFormat.QR_CODE, 128, 128);
    }


}
