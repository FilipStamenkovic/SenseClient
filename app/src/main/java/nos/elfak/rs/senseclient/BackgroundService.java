package nos.elfak.rs.senseclient;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by filip on 27.4.16..
 */
public class BackgroundService extends IntentService
{
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public BackgroundService(String name)
    {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {

    }
}
