import com.sun.org.apache.xpath.internal.functions.FuncFalse;
import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.*;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Feeder;
import model.Message;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static model.Message.Topic.*;

/**
 *created by Shiva Narugula
 * An Gui application for news scrolling which consists of file and control options like faster,resume,forward
 * pause and quit.It has also options for selecting the required topics.
 */


public class RunningHeadline extends Application {

    private Group group; // container for controls and text
    private Feeder feeder; // model class which stores and processes messages
    private List<Timeline> timelines = new ArrayList<>(); // animated messages
    private String str;

    /**
     * given the contents of the message buffer, creates a list
     * of timeline objects with start and end properties for text
     * objects set up, the speed at which each message timeline will
     * be played (the same for all -- the messages are like railroad cars
     * in one train chain)
     */
	private void setUpTimeline(Message.Topic topic1,Message.Topic topic2,Message.Topic topic3,Message.Topic topic4,Message.Topic topic5,Message.Topic topic6,Message.Topic topic7) {
        double displayWidth = 750;
        double offset = displayWidth + 20; // starting x-pos for message stream
        double playtime = 0;
        Queue<Message> messageBuffer = feeder.getBuffer();
        while (!messageBuffer.isEmpty()) {
            Timeline timeline = new Timeline();
            timelines.add(timeline);
            Message latestMessage;
            latestMessage = messageBuffer.poll();
            //switch (topics)
            if (latestMessage.topic == topic1 || latestMessage.topic == topic2 || latestMessage.topic == topic3 || latestMessage.topic == topic4 || latestMessage.topic == topic5 || latestMessage.topic == topic6 || latestMessage.topic == topic7) {
                String messageBody = latestMessage.text + " ";
                Message.Topic color1 = latestMessage.topic;
                Message.Status stat = latestMessage.status;

                //System.out.printf(messageBody);
                double displayHeight = 120;
                Text text = new Text(offset, displayHeight - 13,
                        messageBody);
                // use another font if this one is not available on your system
                text.setFont(Font.font("Tahoma", FontWeight.BLACK, 40));
                //the below fills color as per reuired topic and for emergengy message the red color is used.
                if(color1 == BUSINESS ){
                    if(stat == Message.Status.EMERGENCY) text.setFill(Color.RED);
                    else text.setFill(Color.BLUE);
                }
                else if(color1 == WEATHER){
                    if(stat == Message.Status.EMERGENCY) text.setFill(Color.RED);
                    else text.setFill(Color.BLUE);
                }
                else if(color1 == DOMESTIC){
                    if(stat == Message.Status.EMERGENCY) text.setFill(Color.RED);
                    else text.setFill(Color.BROWN);
                }
                else if(color1 == INTERNATIONAL){
                    if(stat == Message.Status.EMERGENCY) text.setFill(Color.RED);
                    else text.setFill(Color.SKYBLUE);
                }
                else if(color1 == SCITECH){
                    if(stat == Message.Status.EMERGENCY) text.setFill(Color.RED);
                    else text.setFill(Color.ORANGE);
                }
                else if(color1 == SPORT){
                    if(stat == Message.Status.EMERGENCY) text.setFill(Color.RED);
                    else text.setFill(Color.GREEN);
                }
                else{
                    if(stat == Message.Status.EMERGENCY) text.setFill(Color.RED);
                    else text.setFill(Color.YELLOW);
                }
                text.setTextOrigin(VPos.TOP);
                double mesWidth = text.getLayoutBounds().getWidth();
                double playSpeed = 0.2;
                playtime += mesWidth / playSpeed;
                setNewsPieceForRun(text, group, timeline, playtime);
                offset += mesWidth;
            }
        }
    }

    /**
     * defines parameters of (message) timeline
     * @param text is a text object which will be animated
     * @param group is the parent container to which the text is added as child
     * @param tl is a timeline for individual animation of this text
     * @param playtime is the time for tl to be played (no autoreverse, 1 cycle)
     * 
     * tl is defined to remove itself from the timelines list when it is 
     * finished (this may need to be removed for features like "play previous"
     * to be implemented); the reason for this removal is to reduce the memory
     * usage mainly. Also, a diagnostic messages is added to be printed when the
     * tl timeline complets (can be removed with no harm)
     */
	private void setNewsPieceForRun(Text text, Group group, 
				Timeline tl, double playtime) {
        group.getChildren().add(text);
        int mesLength = (int) text.getLayoutBounds().getWidth();

        tl.setCycleCount(1);
        tl.setAutoReverse(false);
        KeyValue kv = new KeyValue(text.xProperty(),
                -10 - mesLength, Interpolator.LINEAR);
        KeyFrame kf = new KeyFrame(Duration.millis(playtime), kv);
        tl.getKeyFrames().add(kf);
        timelines.add(tl);
    }
	
    /**
     * start (resumes) running all (remianing) timeline messages
     */
	private void runNewsStream() {
        timelines.forEach(Animation::play);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
       // Parent root = FXMLLoader.load(getClass().getResource("view/display.fxml"));
        primaryStage.setTitle("News just in...");

        group = new Group();
        // the scene itself
        Scene scene = new Scene(group, 1000, 600);
        scene.getStylesheets().add("view/styles.css");
        BorderPane root1 = new BorderPane();
        //Creation of menu bars
        //File Menubar
        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        root1.setTop(menuBar);
        final FileChooser fileChooser = new FileChooser();
        AtomicReference<Menu> fileMenu;
        fileMenu = new AtomicReference<>(new Menu("File"));
        MenuItem openMenuItem = new MenuItem("Open");
        feeder = new Feeder();// ceating new feeder.
        
       // Control Menu
        Menu Control = new Menu("Control");
        MenuItem run= new MenuItem("Run");
        MenuItem pause = new MenuItem("Pause");
        MenuItem reset= new MenuItem("Reset");
        MenuItem faster= new MenuItem("Faster");
        MenuItem Slower= new MenuItem("Slower");
        MenuItem Next= new MenuItem("Next");
        MenuItem Previous= new MenuItem("Previous");

        // Creation of check boxes
        CheckBox weather = new CheckBox("WEATHER");
        CheckBox international = new CheckBox("INTERNATIONAL");
        CheckBox business = new CheckBox("BUSINESS");
        CheckBox domestic = new CheckBox("DOMESTIC");
        CheckBox scitech = new CheckBox("SCITECH");
        CheckBox sport = new CheckBox("SPORT");
        CheckBox miscell = new CheckBox("MISCELLANEOUS");
        CheckBox all = new CheckBox("ALL");

        final Message.Topic[] w = new Message.Topic[8];

        //menu items methods
        openMenuItem.setOnAction(
                e -> {
                    File file = fileChooser.showOpenDialog(primaryStage);
                    str = file.toString();
                    try {
                        feeder.fillNewsBuffer(Paths.get(str));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                });
        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setOnAction(actionEvent -> Platform.exit());
        pause.setOnAction(actionEvent -> timelines.forEach(Animation::pause));
         //Checkbox methods
        domestic.setOnAction(event -> w[0] = DOMESTIC);
        scitech.setOnAction(event -> w[1] = SCITECH);
        international.setOnAction(event -> w[2]= INTERNATIONAL);
        weather.setOnAction(event -> w[3]= WEATHER);
        miscell.setOnAction(event -> w[4] = MISCELLANEOUS);
        business.setOnAction(event -> w[5] = BUSINESS);
        sport.setOnAction(event -> w[6]= SPORT);
        all.setOnAction(event -> {
            sport.setSelected(true);
            w[6]= SPORT;
            domestic.setSelected(true);
            w[5] = BUSINESS;
            international.setSelected(true);
            w[4] = MISCELLANEOUS;
            scitech.setSelected(true);
            w[3]= WEATHER;
            weather.setSelected(true);
            w[2]= INTERNATIONAL;
            miscell.setSelected(true);
            w[1] = SCITECH;
            business.setSelected(true);
            w[0] = DOMESTIC;
        });
        run.setOnAction((ActionEvent event) ->{
                setUpTimeline(w[0], w[1], w[2], w[3], w[4], w[5], w[6]);
                runNewsStream();
                timelines.forEach(Animation::play);
        });
        reset.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                timelines.clear();
            }
        });

        faster.setOnAction(actionEvent -> timelines.forEach(t1 -> t1.setRate(3)));
        Slower.setOnAction(actionEvent -> timelines.forEach(t1 -> t1.setRate(0.85)));
        // examples of call-backs to control the timeline
        // via key-board; can be removed when menu and proper controls
        // like check-boxes are added
		scene.onKeyPressedProperty().set(ke -> {
            if (ke.isMetaDown() && ke.getCode() == KeyCode.Q)
                Platform.exit();
            else if (ke.getCode() == KeyCode.P)
                timelines.forEach(Animation::pause);
            else if (ke.getCode() == KeyCode.R)
                timelines.forEach(Animation::play);
        });

        fileMenu.get().getItems().addAll(openMenuItem,
                new SeparatorMenuItem(), exitMenuItem);
        Control.getItems().addAll(run,pause,reset,new SeparatorMenuItem(),faster,Slower,Next,Previous);
        menuBar.getMenus().addAll(fileMenu.get(),Control);


        /*creation tool bar which consists of all checkboxes */

        ToolBar tool = new ToolBar();
        tool.relocate(0,23);
        tool.setOrientation(Orientation.HORIZONTAL);
        tool.getItems().addAll(new Separator(),weather,new Separator(), international, business,domestic,scitech,sport,miscell,all);

        group.getChildren().add(0,root1);
        group.getChildren().add(1,tool);
		primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> Platform.exit());

    }

    public static void main(String[] args) throws IOException {
        launch(args);
    }
}
