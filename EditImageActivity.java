package recorder.soundrecorder.com.fourarc.camfilter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditImageActivity extends AppCompatActivity implements FilterListFragment.FiltersListFragmentListener, EditImageFragment.EditImageFragmentListener {



    public static String imgUrl ;
    public static Bitmap phobitmap ;
    TextView txt_done;
    ImageView image_preview,img_back;
    ViewPager viewpager;


    private String mAppend = "file:/";
    private int imageCount = 0;
    private Intent intent;
    private Bitmap bitmap;
    Bitmap myBitmap;

    TextView edt_Text;

    // modified image values
    int brightnessFinal = 0;
    float saturationFinal = 1.0f;
    float contrastFinal = 1.0f;
    public static Bitmap finalbitmap ;
    private static int SPLASH_TIME_OUT = 2000;

    private static final String TAG = EditImageActivity.class.getSimpleName();

    public static final String IMAGE_NAME = "dog.jpg";

    public static final int SELECT_GALLERY_IMAGE = 101;

    @BindView(R.id.image_preview)
    ImageView imagePreview;

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.coordinator_layout)
    LinearLayout coordinatorLayout;

    Bitmap originalImage;
    // to backup image with filter applied
    Bitmap filteredImage;

    // the final image after applying
    // brightness, saturation, contrast
    Bitmap finalImage;

    FilterListFragment filtersListFragment;
    EditImageFragment editImageFragment;

    // load native image filters library
    static {
        System.loadLibrary("NativeImageProcessor");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        ButterKnife.bind(this);

        txt_done = (TextView)findViewById(R.id.txt_done);
        image_preview = (ImageView)findViewById(R.id.image_preview);
        img_back = (ImageView)findViewById(R.id.img_back);
        tabLayout = (TabLayout)findViewById(R.id.tabs);
        viewpager = (ViewPager)findViewById(R.id.viewpager);

        txt_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapUtils.finalbitmap = finalbitmap;
                storeImage(finalbitmap);
            }
        });
        Intent intent = getIntent();
        Bitmap bitmap = (Bitmap) intent.getParcelableExtra("selected_image");
        imgUrl = intent.getStringExtra("selected_image");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (imgUrl == null){
                    originalImage = phobitmap;
                    filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
                    finalbitmap = originalImage.copy(Bitmap.Config.ARGB_8888, true);
                    image_preview.setImageBitmap(originalImage);

                }else {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    options.inSampleSize = calculateInSampleSize(options, 100,100);
                    options.inJustDecodeBounds = false;
                    Bitmap smallBitmap = BitmapFactory.decodeFile(imgUrl, options);

                    originalImage= smallBitmap;
                    filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
                    finalbitmap = originalImage.copy(Bitmap.Config.ARGB_8888, true);
                    image_preview.setImageBitmap(originalImage);
                }
            }
        }, SPLASH_TIME_OUT);

        setupViewPager(viewpager);
        tabLayout.setupWithViewPager(viewpager);



    }

    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }

    private  File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagAdapter adapter = new ViewPagAdapter(getSupportFragmentManager());

        // adding filter list fragment
        filtersListFragment = new FilterListFragment();
        filtersListFragment.setListener(this);

        // adding edit image fragment
        editImageFragment = new EditImageFragment();
        editImageFragment.setListener(this);

        adapter.addFragment(filtersListFragment, "filters");
        adapter.addFragment(editImageFragment, "edit");

        viewPager.setAdapter(adapter);
    }

    class ViewPagAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }





    @Override
    public void onBrightnessChanged(int brightness) {
        brightnessFinal = brightness;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        image_preview.setImageBitmap(myFilter.processFilter(finalbitmap.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onSaturationChanged(float saturation) {
        saturationFinal = saturation;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        image_preview.setImageBitmap(myFilter.processFilter(finalbitmap.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onContrastChanged(float contrast) {
        contrastFinal = contrast;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(contrast));
        image_preview.setImageBitmap(myFilter.processFilter(finalbitmap.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditCompleted() {
        final Bitmap bitmap = filteredImage.copy(Bitmap.Config.ARGB_8888, true);

        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new ContrastSubFilter(contrastFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
        finalbitmap = myFilter.processFilter(bitmap);
    }

    @Override
    public void onFilterSelected(Filter filter) {
        resetControls();

        // applying the selected filter
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        // preview filtered image
        image_preview.setImageBitmap(filter.processFilter(filteredImage));

        finalbitmap = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
    }

    private void resetControls() {
        if (editImageFragment != null) {
            editImageFragment.resetControls();
        }
        brightnessFinal = 0;
        saturationFinal = 1.0f;
        contrastFinal = 1.0f;
    }
}
