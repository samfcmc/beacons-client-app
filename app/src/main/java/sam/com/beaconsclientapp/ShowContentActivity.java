package sam.com.beaconsclientapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class ShowContentActivity extends Activity {

    private WebView webView;
    private BeaconClientApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAsFullscreenActivity();
        setContentView(R.layout.activity_show_content);

        this.application = (BeaconClientApplication) getApplication();
        this.webView = (WebView) findViewById(R.id.show_content_content_webview);
        String url = getIntent().getStringExtra("url");

        openBrowser(url);
    }

    private void setAsFullscreenActivity() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void openBrowser(String url) {
        this.webView.getSettings().setJavaScriptEnabled(true);
        String trimUrl = url.trim();
        String finalUrl = url;

        if(!trimUrl.startsWith("http")) {
            finalUrl = "http://" + trimUrl;
        }

        this.webView.setWebViewClient(new WebViewClient());

        this.webView.loadUrl(finalUrl);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_content, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
