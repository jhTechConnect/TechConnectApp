package org.centum.techconnect.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.centum.techconnect.R;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageViewActivity extends AppCompatActivity {

    public static final String EXTRA_PATH = "path";
    @Bind(R.id.photoImageView)
    GifImageView imageView;

    private PhotoViewAttacher attacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        ButterKnife.bind(this);

        attacher = new PhotoViewAttacher(imageView);

        if (getIntent() != null && getIntent().getStringExtra(EXTRA_PATH) != null) {
            String path = getIntent().getStringExtra(EXTRA_PATH);
            if (path.toLowerCase().endsWith(".gif")) {
                try {
                    GifDrawable drawable = new GifDrawable(path);
                    imageView.setImageDrawable(drawable);
                } catch (IOException e) {
                    imageView.setImageResource(android.R.drawable.stat_notify_error);
                    e.printStackTrace();
                }
                attacher.update();
            } else {
                Picasso.with(this)
                        .load(new File(path))
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                attacher.update();
                            }

                            @Override
                            public void onError() {
                                imageView.setImageResource(android.R.drawable.stat_notify_error);
                                attacher.update();
                            }
                        });
            }
        }
    }
}
