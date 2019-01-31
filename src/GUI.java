import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.stream.Collectors;


public class GUI extends JFrame {
    private Controller control;
    private int articleWordAmount; // default number for amount of words used in article game

    public GUI(int width, int height, Controller control){
        articleWordAmount = 10;
        this.control = control;
        initGui(width, height);

    }

    /**
     * Method which sets the JPanel that is responsible for modifying entries
     * @param panel the container panel which is the content pane of the Window frame
     */
    private void setJPanel(JPanel panel){
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        addInfo(panel, "On this region you can modify all entries in your list of entries");
        addEntryButtons(panel);
        addShowButton(panel);
        panel.setVisible(true);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    private void setGamingArea(JPanel panel){
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        addInfo(panel, "This area is for the game");
        // create a game with a default setting of 10 words
        addStartButton(panel);
        addOptionsButton(panel);
        //addOptionsButtons(panel); //TODO: implement
        panel.setVisible(true);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));


    }
    private void addOptionsButton(Container panel){
        JButton options = new JButton("Set amount of Words");
        options.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("Options");
                Container content = frame.getContentPane();
                content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
                JLabel info = new JLabel("Please enter number of Words to play with below");
                JTextField number = new JTextField("");
                number.setMaximumSize(new Dimension(125,100));
                JButton ok = new JButton("OK");
                ok.addActionListener(new ActionListener(){
                   public void actionPerformed(ActionEvent e){
                       int amount = Integer.parseInt(number.getText());
                       articleWordAmount = amount;
                       frame.dispose();
                    }
                });



                content.add(info);
                content.add(number);
                content.add(ok);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            }
        });
        panel.add(options);
    }
    private void addStartButton(Container pane){
        JButton start = new JButton("Start the game");
        start.addActionListener(e -> control.startGame(articleWordAmount));
        pane.add(start);
    }

    /**
     * method which adds a button to show all entries
     * @param pane the container panel which is the adding jpanel
     */
    private void addShowButton(Container pane){
        JButton showEntries = new JButton("Show entries");
        showEntries.addActionListener(e -> showEntries());
        pane.add(showEntries);
    }

    /**
     * method which makes a new window showing all entries in the file
     */
    private void showEntries(){
        if (control.getEntries().size() != 0) {
            JFrame entryWindow = new JFrame();
            Container pane = entryWindow.getContentPane();
            pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

            //Button which can be pressed to close window too
            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> closeFrame(entryWindow));

            ArrayList<JLabel> entries = new ArrayList<>();
            for (Entry x : control.getEntries()) {
                entries.add(new JLabel(x.getEntryforFile()));
            }

            for (JLabel i : entries)
                pane.add(i);

            pane.add(closeButton);
            entryWindow.setLocationRelativeTo(null);
            entryWindow.setVisible(true);
            entryWindow.pack();
            entryWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
        else {
            JFrame frame = new JFrame("Empty file");
            Container content = frame.getContentPane();
            JOptionPane.showMessageDialog(content,"Your list of entries is empty");
            frame.pack();
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
    }

    private void closeFrame(JFrame window){
        window.dispose();
    }


    private void addInfo(Container pane, String message ){
        JLabel infoText = new JLabel(message);
        pane.add(infoText);
        pane.add(Box.createRigidArea(new Dimension(1,20)));

    }

    /**
     * method responsible for adding buttons that modify the file
     * @param panel container jpanel
     */
    private void addEntryButtons(JPanel panel){
        JButton oneEntry = new JButton("Add one entry");
        JButton moreEntry = new JButton("Add multiple entries");

        // adds the action listener to button which shows a message if input entry is already in file
        oneEntry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AdderWindow window = new AdderWindow(control, 1);
            }
        });

        moreEntry.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                int number = Integer.parseInt(JOptionPane.showInputDialog("How many numbers do you want to add"));
                AdderWindow window = new AdderWindow(control, number);
            }
        });
        panel.add(oneEntry);
        panel.add(moreEntry);
    }



    public static void addResultButton(Game game, Container panel, ArrayList<GamingEntry> gameEntries){
        JButton result = new JButton("Click to see results");
        result.addActionListener(new ActionListener() {
            ArrayList<Boolean> results = new ArrayList<>();
            @Override
            public void actionPerformed(ActionEvent e) {
                for(GamingEntry x : gameEntries){
                    String input = x.getFieldArticle().trim().toLowerCase();
                    String shouldBe = x.getArticle();
                    boolean isGood = game.checkPoint(input,shouldBe);
                    results.add(isGood);
                }
                showResults();
            }
            private void showResults(){
                int i = 0;
                for(boolean t: results){
                    gameEntries.get(i).setMarker(t);
                    gameEntries.get(i).makeVisible();
                    i++;
                }
                String score = "You scored " + Integer.toString(game.getPointsReached()) + " out of " + Integer.toString(game.getMaxPoints()) + " points!";
                JLabel scoreText = new JLabel(score);
                JLabel percentage = new JLabel(Double.toString(game.getPercentage() * 100 ) + "%" );
                Object[] message = {scoreText, percentage};
                JOptionPane.showMessageDialog(panel, message);
                //replace showResultButton with EndApplication button
                addEndButton(panel);
            }

            private void addEndButton(Container panel){
                //remove showResult button, we know its the last item
                panel.remove(panel.getComponents().length -1);
                Component[] comp = panel.getComponents();
                for(Component x : comp)
                    System.out.println(x);
                panel.revalidate();
                panel.repaint();
                JButton endButton = new JButton("End game");
                JFrame window = Controller.getJFrame(panel);

                endButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        window.dispose();
                    }
                });
                panel.add(endButton);
            }
        });
        panel.add(result);
    }


    /**
     * sets the basic window
     * @param width window width
     * @param height window height
     */
    private void initGui(int width, int height){
        setTitle("Learning articles");
        setSize(new Dimension(width, height));
        Container pane = getContentPane();
        pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));

        adderPane(pane);
        gamingPane(pane);

        setVisible(true);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

    }

    /**
     * sets the pane which holds all components to modify or show entries
     * @param pane container which is the window jframe in this case (content pane of it)
     */
    private void adderPane(Container pane){
        JPanel entryAdder = new JPanel();
        setJPanel(entryAdder);
        pane.add(entryAdder);
    }

    private void gamingPane(Container pane){
        JPanel gamingArea = new JPanel();
        setGamingArea(gamingArea);
        pane.add(gamingArea);
    }

}
