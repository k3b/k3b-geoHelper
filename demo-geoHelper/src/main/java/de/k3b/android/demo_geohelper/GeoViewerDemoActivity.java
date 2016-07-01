package de.k3b.android.demo_geohelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import java.text.MessageFormat;
import de.k3b.geo.api.IGeoPointInfo;
import de.k3b.geo.io.GeoUri;

public class GeoViewerDemoActivity extends Activity {
    private static final String NL = "\n";
    private final GeoUri parser = new GeoUri(GeoUri.OPT_FORMAT_REDUNDANT_LAT_LON | GeoUri.OPT_PARSE_INFER_MISSING);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        StringBuilder message = new StringBuilder();
        Intent intent = getIntent();
        
        if (intent != null) {
            Object uri = intent.getData();
            message.append(MessageFormat.format("Received action:{0}\n - data:{1}\n", intent.getAction(), uri));

            IGeoPointInfo geo = (uri != null) ? parser.fromUri(uri.toString()) : null;
            if (geo != null) {
                message.append(MessageFormat.format(
                        "\nlat:{0} lon:{1} zoom:{2} name:{3}\n"
                        , geo.getLatitude(), geo.getLongitude(), geo.getZoomMin(), geo.getName()));

            }
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(this.getClass().getSimpleName());
        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    final DialogInterface dialog,
                                    final int id) {
                                finish();
                            }
                        }
                );

        final AlertDialog alert = builder.create();
        alert.show();

    }
}
