package com.foxycode.testapp.Activity;

import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import com.foxycode.testapp.Backend.BackendService;
import com.foxycode.testapp.DataBase.Contract.ContentUs;
import com.foxycode.testapp.DataBase.Provider.ContentUsProvider;
import com.foxycode.testapp.Fragment.MainFragment;
import com.foxycode.testapp.Model.ContentGeneric;
import com.foxycode.testapp.R;
import com.foxycode.testapp.Fragment.NavigationDrawerFragment;

import java.util.List;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, MainFragment.ComWithActivity {

    SharedPreferenceManager mSharedPref;
    private boolean mIsBound = false;

    boolean mIsDeleteButtonActiv = false;
    private BackendService mService;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    public void changeMenu(boolean listEmpty){
        mIsDeleteButtonActiv = !listEmpty;
        invalidateOptionsMenu();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        doBindService();

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance("blabla"))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.app_name));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            //getMenuInflater().inflate(R.menu.test, menu);

            if (mIsDeleteButtonActiv)
                getMenuInflater().inflate(R.menu.main, menu);
            else
                getMenuInflater().inflate(R.menu.main_desactivated, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            Toast.makeText(this, "action delete", Toast.LENGTH_SHORT).show();
            MainFragment articleFrag = (MainFragment)
                    getSupportFragmentManager().findFragmentById(R.id.container);

            List<ContentGeneric> list = articleFrag.mAdapter.getListContent();
            String where ="";
            String whereLocal ="";
            for(ContentGeneric c:list)
                 if(c.getIdServerDb()!=-4)
                     where += ContentUs.CONTENT_ID_SERVERDB + " =  "+c.getIdServerDb() +" OR ";

            for(ContentGeneric c:list)
                whereLocal += ContentUs.ID + " =  "+c.getId() +" OR ";

             mService.deleteFiles(where.substring(0,where.length()-4),whereLocal.substring(0,where.length()-4));

            Log.v("TAG", "in delete " + where);
            getContentResolver().delete(ContentUsProvider.CONTENT_URI,whereLocal.substring(0,where.length()-4), null);
            articleFrag.mAdapter.clearList();

            changeMenu(true);
            return true;
        }
        if (id == R.id.action_settings) {
            return true;
        }
        if (item.getItemId() == R.id.action_example) {
            Toast.makeText(this, "Example action.", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void encryptImageFile(String imagePath) {
        if(mService==null)
        {
            Log.v("tag","service is null");
        }
        mService.uploadFromUrl(imagePath);
    }

    @Override
    public void encryptSendMessage(String mess) {
        mService.uploadText(mess);
    }

    /**
     * Service Related
     */

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mService = ((BackendService.LocalBinder)service).getService();

            // Tell the user about this for our demo.
            Toast.makeText(MainActivity.this, R.string.local_service_connected,
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mService = null;
            Toast.makeText(MainActivity.this, R.string.local_service_disconnected,
                    Toast.LENGTH_SHORT).show();
        }
    };

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(new Intent(MainActivity.this,
                BackendService.class), mConnection, Context.BIND_AUTO_CREATE);
       // mService = ((BackendService.LocalBinder)service).getService();
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onStop();
        doUnbindService();
    }
    @Override
    protected void onStart() {
        super.onStart();
     }


}
