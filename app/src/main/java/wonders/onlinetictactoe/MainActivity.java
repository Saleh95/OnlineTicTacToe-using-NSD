package wonders.onlinetictactoe;

import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import wonders.onlinetictactoe.board.TTTBoard;
import wonders.onlinetictactoe.nsdConnection.NsdConnection;
import wonders.onlinetictactoe.nsdConnection.NsdHelper;

public class MainActivity extends AppCompatActivity {

    NsdHelper mNsdHelper;

    private TextView mStatusView;
    private Handler mUpdateHandler;

    public static final String TAG = "NsdChat";

    NsdConnection mConnection;
    private Button buttons[];
    private int Turn=2;
    private TTTBoard board;
    private int state;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        board = TTTBoard.getInstance();

        buttons = new Button[9];
        buttons[0] = (Button)findViewById(R.id.button1);
        buttons[1] = (Button)findViewById(R.id.button2);
        buttons[2] = (Button)findViewById(R.id.button3);
        buttons[3] = (Button)findViewById(R.id.button4);
        buttons[4] = (Button)findViewById(R.id.button5);
        buttons[5] = (Button)findViewById(R.id.button6);
        buttons[6] = (Button)findViewById(R.id.button7);
        buttons[7] = (Button)findViewById(R.id.button8);
        buttons[8] = (Button)findViewById(R.id.button9);

        for (int i=0; i<TTTBoard.size; ++i){
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i=0; i<TTTBoard.size; ++i)
                        if (buttons[i]==v&&board.getPlayer()==Turn){
                            gameplay(i);
                            display();
                            buttons[i].setEnabled(false);
                        }
                    state= board.Checker();
                    if(state!=-1){
//                        textView.setText("Player"+board.Checker()+" Won");
                        if(Turn==state){
                            Toast.makeText(getApplicationContext(),"You win",Toast.LENGTH_LONG).show();}
                        else{
                            Toast.makeText(getApplicationContext(),"You lose",Toast.LENGTH_LONG).show();
                        }

                        disable();
                    }
                }
            });}

        display();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



//        mStatusView = (TextView) findViewById(R.id.status);

        mUpdateHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Integer pos = msg.getData().getInt("pos");
                update(pos);
            }
        };

        mConnection = new NsdConnection(mUpdateHandler);

        mNsdHelper = new NsdHelper(this);
        mNsdHelper.initializeNsd();

        Advertise();

    }

    private void update(Integer pos) {
        board.set(pos.intValue());
        display();
    }


    public void Advertise() {
        // Register service
        if(mConnection.getLocalPort() > -1) {
            mNsdHelper.registerService(mConnection.getLocalPort());
        } else {
            Log.d(TAG, "ServerSocket isn't bound.");
        }
    }

    public void clickDiscover(View v) {
        if(mNsdHelper!=null){
        mNsdHelper.discoverServices();}
    }

    public void clickConnect(View v) {
        NsdServiceInfo service = mNsdHelper.getChosenServiceInfo();
        this.Turn = 1;
        if (service != null) {
            Log.d(TAG, "Connecting.");
            mConnection.connectToServer(service.getHost(),
                    service.getPort());
        } else {
            Log.d(TAG, "No service to connect to!");
        }
    }

    private void gameplay(Integer pos){
        board.set(pos.intValue());
        mConnection.sendMessage(pos);
    }

//    public void clickSend(View v) {
//        EditText messageView = (EditText) this.findViewById(R.id.chatInput);
//        if (messageView != null) {
//            String messageString = messageView.getText().toString();
//            if (!messageString.isEmpty()) {
//                mConnection.sendMessage(messageString);
//            }
//            messageView.setText("");
//        }
//    }

    public void addChatLine(String line) {
        mStatusView.append("\n" + line);
    }

    @Override
    protected void onPause() {
        if (mNsdHelper != null) {
            mNsdHelper.stopDiscovery();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mNsdHelper != null) {
            mNsdHelper.discoverServices();
        }
    }

    @Override
    protected void onDestroy() {
        mNsdHelper.tearDown();
        mConnection.tearDown();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void display(){
        for (int i=0; i<TTTBoard.size; ++i){
            char status = board.get(i);
            if (status==TTTBoard.Empty)
                buttons[i].setBackgroundResource(R.mipmap.empty);
            if (status==TTTBoard.P1)
                buttons[i].setBackgroundResource(R.mipmap.o);
            if (status==TTTBoard.P2)
                buttons[i].setBackgroundResource(R.mipmap.x);
        }
    }

    public void disable(){
        for(Button button: buttons)
            button.setEnabled(false);
    }


    private void reset() {
        for(Button button: buttons){
            button.setBackgroundResource(R.mipmap.empty);
            button.setEnabled(true);
        }

        board.reset();

    }
}
