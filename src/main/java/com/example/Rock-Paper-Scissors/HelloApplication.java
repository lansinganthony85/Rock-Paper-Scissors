package com.example.project_2_lansing;

import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Random;

/**
 * Program that implements a rock paper scissors game.
 * Bonus implementations:
 * Difficulty modes
 * Options to change themes
 * Random animated popup when rock is selected 5 times in a row
 * Chart Feature
 * Sorry no Spock but live long and prosper!
 * @author Anthony Lansing
 * @version 07/24/2022
 */
public class HelloApplication extends Application {

    int wins = 0;
    int losses = 0;
    int ties = 0;
    int roundsToPlay = 0;
    int currentRound = 1;
    String name;
    double chanceToWin;
    final int WIDTH = 500;
    final int HEIGHT = 700;
    int rockOutCount = 0;

    final String DEFAULT_FONT = "Helvetica";
    final double TITLE_SIZE = 24.0f;
    final double TEXT_SIZE = 18.0f;
    final double THEME_SIZE = 16.0f;
    final double DIFFICULTY_SIZE = 12.0f;

    /*
    * Initialize controls so that there is global access.
     */

    //Load all images
    Image rockImage = new Image("file:src/main/resources/images/rock.png", (double) WIDTH/5, 0, true, true);
    Image paperImage = new Image("file:src/main/resources/images/paper.png", (double) WIDTH/5+10, 0, true, true);
    Image scissorsImage = new Image("file:src/main/resources/images/scissors.png", (double) WIDTH/5, 0, true, true);
    Image rockImageColor = new Image("file:src/main/resources/images/rock_selected.png", (double) WIDTH/5, 0, true, true);
    Image paperImageColor = new Image("file:src/main/resources/images/paper_selected.png", (double) WIDTH/5+10, 0, true, true);
    Image scissorsImageColor = new Image("file:src/main/resources/images/scissors_selected.png", (double) WIDTH/5, 0, true, true);
    Image rockOutImage = new Image("file:src/main/resources/images/rock_out.png", (double) WIDTH/5, 0, true, true);

    //Palettes for color themes, listed in order of background color, text color, error text color, and title color
    String[] brightPalette = { "F8DCB0", "FC5E70", "FF0000", "FBBC54" };
    String[] easterPalette = { "EFF1DB", "D3B5E5", "FF0000", "FFD4DB" };
    String[] metalPalette = { "ADB3BD", "10151B", "4B5C74", "7C7E7E" };

    /*
     * Initialize GUI elements for the main menu. This makes it easier to change these elements later on
     */

    // Initialize the Labels for the main menu
    Label themeLabel = makeLabel("Themes:", THEME_SIZE);
    Label difficultyLabel = makeLabel("Choose your difficulty:", TITLE_SIZE);
    Label nameErrorLabel = makeLabel("", TEXT_SIZE);
    Label roundsErrorLabel = makeLabel("", TEXT_SIZE);

    // Initialize the Buttons for the main menu
    ToggleButton metalButton = makeButton("Metal", THEME_SIZE);
    ToggleButton brightButton = makeButton("Bright", THEME_SIZE);
    ToggleButton easterButton = makeButton("Easter", THEME_SIZE);
    ToggleButton playButton = makeButton("BEGIN!", THEME_SIZE);

    // Initialize the RadioButtons for the main menu
    RadioButton babyEasyButton = makeRadioButton("Can I win, please?", DIFFICULTY_SIZE);
    RadioButton easyButton = makeRadioButton("Easy", DIFFICULTY_SIZE);
    RadioButton normalButton = makeRadioButton("Normal", DIFFICULTY_SIZE);
    RadioButton hardButton = makeRadioButton("Hard", DIFFICULTY_SIZE);
    RadioButton superHardButton = makeRadioButton("You are not ready", DIFFICULTY_SIZE);

    // Initialize the TextFields for the main menu
    TextField nameField = makeTextField("Please enter your name...");
    TextField roundsField = makeTextField("Please enter the number of rounds to play...");

    // Initialize the VBox to hold all elements. The background color will be changing later.
    VBox mainMenu = makeVBox();

    /*
     * Initialize the GUI elements for the game scene. This makes it easier to make changes to these later.
     */

    // Initialize the Labels
    Label welcomeLabel = makeLabel("", TITLE_SIZE);
    Label roundsLabel = makeLabel("", TITLE_SIZE);
    Label winLabel = makeLabel("", TITLE_SIZE);
    Label lossLabel = makeLabel("", TITLE_SIZE);
    Label tieLabel = makeLabel("", TITLE_SIZE);
    Label gameOverLabel = makeLabel("", TITLE_SIZE);

    // Initialize the buttons
    ToggleButton resetButton = makeButton("RESET", TITLE_SIZE);
    ToggleButton menuButton = makeButton("MAIN MENU", TITLE_SIZE);
    ToggleButton dataButton = makeButton("STATS", TITLE_SIZE);

    ImageView rockSelectImage = new ImageView(rockImage);
    ImageView paperSelectImage = new ImageView(paperImage);
    ImageView scissorsSelectImage = new ImageView(scissorsImage);

    AnchorPane animationPane = new AnchorPane();

    // Initialize the VBox to hold all elements to change color easily later.
    VBox gameBox = makeVBox();
    
    /*
     * Initialize the GUI elements for the random pop-up
     */
    Label rockOutLabel = makeLabel("ROCK OUT!", TITLE_SIZE);
    ImageView rockOutImageView = new ImageView(rockOutImage);
    VBox rockOutBox = makeVBox(rockOutImageView, rockOutLabel);

    /*
     * Initialize the GUI elements for the pie chart
     */
    StackPane chartPane = new StackPane();

    @Override
    public void start(Stage stage) throws IOException {

        //Load the default color theme
        loadTheme(brightPalette);

        /*
         * Build the main menu scene with implementation.
         */

        //Build the HBox to hold the GUI elements that change the color theme.
        ToggleGroup themeGroup = new ToggleGroup();
        metalButton.setToggleGroup(themeGroup);
        brightButton.setToggleGroup(themeGroup);
        easterButton.setToggleGroup(themeGroup);
        HBox themeBox = makeHBox(1, Pos.CENTER_RIGHT, themeLabel, metalButton, brightButton, easterButton);

        // implement what happens when the theme buttons are pressed
        metalButton.setOnAction(e -> loadTheme(metalPalette));
        brightButton.setOnAction(e -> loadTheme(brightPalette));
        easterButton.setOnAction(e -> loadTheme(easterPalette));

        // Build the logo by first loading images into ImageViews
        ImageView rockLogoImage = new ImageView(rockImageColor);
        ImageView paperLogoImage = new ImageView(paperImageColor);
        paperLogoImage.setRotate(120);
        ImageView scissorsLogoImage = new ImageView(scissorsImageColor);
        scissorsLogoImage.setRotate(240);

        // Anchor the images into the correct positions for the logo
        AnchorPane logoPane = new AnchorPane(rockLogoImage, paperLogoImage, scissorsLogoImage);

        //Create the text for the logo
        Text gameLogo = new Text();
        gameLogo.setText("ROCK!\nPAPER! SCISSORS!");
        gameLogo.setFill(Color.web("e0ce57"));
        gameLogo.setStroke(Color.BLACK);
        gameLogo.setFont(new Font("Impact", 60.0));
        InnerShadow goldShadow = new InnerShadow();
        goldShadow.setOffsetX(2);
        goldShadow.setOffsetY(2);
        goldShadow.setColor(Color.WHITE);
        gameLogo.setEffect(goldShadow);
        gameLogo.setTextAlignment(TextAlignment.CENTER);

        // Put the images and text on a stack pane for the final logo
        StackPane logoStackPane = new StackPane(logoPane, gameLogo);
        StackPane.setMargin(gameLogo, new Insets(rockImage.getHeight(), 0, 0, 0));

        // keep the logo together if the screen gets resized.
        logoPane.widthProperty().addListener(e -> anchorImages(logoPane, rockLogoImage, paperLogoImage, scissorsLogoImage));
        logoPane.heightProperty().addListener(e -> anchorImages(logoPane, rockLogoImage, paperLogoImage, scissorsLogoImage));

        // set the normal difficulty as the default option
        normalButton.setSelected(true);

        // Put the difficulty radio buttons into a ToggleGroup
        ToggleGroup difficultyGroup = new ToggleGroup();
        babyEasyButton.setToggleGroup(difficultyGroup);
        easyButton.setToggleGroup(difficultyGroup);
        normalButton.setToggleGroup(difficultyGroup);
        hardButton.setToggleGroup(difficultyGroup);
        superHardButton.setToggleGroup(difficultyGroup);

        // make the HBox to hold the difficulty options
        HBox difficultyBox = makeHBox(10, Pos.CENTER, babyEasyButton, easyButton, normalButton, hardButton, superHardButton);

        // Put all the elements into the VBox and display the scene.
        mainMenu.getChildren().addAll(themeBox, logoStackPane, difficultyLabel, difficultyBox, nameField, roundsField, playButton, nameErrorLabel, roundsErrorLabel);
        Scene scene = new Scene(mainMenu, WIDTH, HEIGHT);
        stage.setTitle("Rock, Paper, Scissors");
        stage.setScene(scene);

        /*
         * Put together the game scene.
         */

        // Get the rock, paper, scissor buttons ready for the game
        paperSelectImage.setRotate(120);
        scissorsSelectImage.setRotate(240);

        AnchorPane selectPane = new AnchorPane(rockSelectImage, paperSelectImage, scissorsSelectImage, roundsLabel);

        // Implement event handler to keep the rock, paper, scissor buttons in place.
        selectPane.widthProperty().addListener(e -> anchorImages(selectPane, rockSelectImage, paperSelectImage, scissorsSelectImage));
        selectPane.heightProperty().addListener(e -> anchorImages(selectPane, rockSelectImage, paperSelectImage, scissorsSelectImage));

        // Implement the event handlers for the mouse hovers over an option.
        // When the mouse hovers over an image, the image changes color
        rockSelectImage.hoverProperty().addListener(e -> {
            if (rockSelectImage.isHover())
                rockSelectImage.setImage(rockImageColor);
            else
                rockSelectImage.setImage(rockImage);
        });
        paperSelectImage.hoverProperty().addListener(e -> {
            if (paperSelectImage.isHover())
                paperSelectImage.setImage(paperImageColor);
            else
                paperSelectImage.setImage(paperImage);
        });
        scissorsSelectImage.hoverProperty().addListener(e -> {
            if (scissorsSelectImage.isHover())
                scissorsSelectImage.setImage(scissorsImageColor);
            else
                scissorsSelectImage.setImage(scissorsImage);
        });

        // Prepare the pane that will hold the animations for the game
        animationPane.setPrefSize(WIDTH, 200);

        // Prepare the scene for the random pop-up
        Scene rockScene = new Scene(rockOutBox, 200, 200);

        // Implement the event handler for when the player makes a choice
        rockSelectImage.setOnMouseClicked(e -> {
            playGame(0);
            
            // Check if rock has been selected 5 times in a row and display an animation
            rockOutCount++;
            if (rockOutCount >= 5) {
                
                //Prepare the stage for the random pop-up
                Stage rockStage = new Stage();

                // Prepare the initial image rotation
                rockOutImageView.setRotate(45);

                //Set up the event handler for the KeyFrame so that the image moves back and forth
                EventHandler<ActionEvent> changeFrame = ev -> {
                    if (rockOutImageView.getRotate() == -45)
                        rockOutImageView.setRotate(45);
                    else
                        rockOutImageView.setRotate(-45);
                };

                // Prepare the animation to run based on the event handler which will close by itself after 10 cycles
                Timeline rockAnimation = new Timeline(new KeyFrame(Duration.millis(200), changeFrame));
                rockAnimation.setOnFinished(ov -> rockStage.close());
                rockAnimation.setCycleCount(10);
                rockAnimation.play();

                rockStage.setScene(rockScene);
                rockStage.show();
                rockOutCount = 0;
            }
        });
        paperSelectImage.setOnMouseClicked(e -> {
            playGame(1);
            rockOutCount = 0;
        });
        scissorsSelectImage.setOnMouseClicked(e -> {
            playGame(2);
            rockOutCount = 0;
        });

        // prepare the HBox that holds the game data
        HBox dataBox = makeHBox(10, Pos.CENTER, winLabel, lossLabel, tieLabel);

        // prepare the HBox that holds the menu option buttons
        HBox buttonBox = makeHBox(10, Pos.CENTER, resetButton, menuButton, dataButton);

        // put everything together in a VBox and put it in a scene
        gameBox.getChildren().addAll(welcomeLabel, selectPane, animationPane, gameOverLabel, dataBox, buttonBox);
        Scene gameScene = new Scene(gameBox, WIDTH, HEIGHT);

        // implement what happens when the reset button is pressed
        resetButton.setOnAction(e -> {
            resetGame();
            resetButton.setSelected(false);
        });

        // implement what happens when the menu button is pressed
        menuButton.setOnAction(e -> {
            resetGame();
            stage.setScene(scene);
            menuButton.setSelected(false);
        });

        // implement what happens when the play button is pressed
        playButton.setOnAction(e -> {
            try {
                nameErrorLabel.setText("");
                roundsErrorLabel.setText("");
                name = nameField.getText();
                if (name.length() == 0)
                    nameErrorLabel.setText("You must enter a name.");
                roundsToPlay = Integer.parseInt(roundsField.getText());
                if (roundsToPlay < 0)
                    roundsErrorLabel.setText("Please enter a positive integer.");
                if (name.length() != 0 && roundsToPlay > 0) {
                    nameErrorLabel.setText("");
                    roundsErrorLabel.setText("");
                    stage.setScene(gameScene);
                    updateLabels();
                }

                // assign the difficulty factor based on selection. When normal is selected, 0.5 is only used to identify
                // that it is normal difficulty.
                if (babyEasyButton.isSelected())
                    chanceToWin = 0.8f;
                else if (easyButton.isSelected())
                    chanceToWin = 0.6f;
                else if (normalButton.isSelected())
                    chanceToWin = 0.5f;
                else if (hardButton.isSelected())
                    chanceToWin = 0.4f;
                else if (superHardButton.isSelected())
                    chanceToWin = 0.2f;

            }
            catch (NumberFormatException ex) {
                roundsErrorLabel.setText("You must provide an integer for the number of rounds.");
            }
            finally {
                playButton.setSelected(false);
            }
        });

        /*
         * Create the stage and scene for a pie chart that displays the game's current stats
         */
        PieChart chart = new PieChart();
        chart.setTitle("Current Stats");
        chartPane.getChildren().add(chart);
        Stage chartStage = new Stage();
        Scene chartScene = new Scene(chartPane);
        chartStage.setTitle("Game Stats");
        chartStage.setScene(chartScene);

        // implement what happens when the status button is pressed. Pie chart data is passed and a pie chart is displayed
        dataButton.setOnAction(e -> {
            chart.setData(FXCollections.observableArrayList(new PieChart.Data("Wins", wins), new PieChart.Data("Losses", losses), new PieChart.Data("Ties", ties)));
            chartStage.show();
            dataButton.setSelected(false);
        });

        stage.show();
    }

    /*
     * Set the game back to initial values.
     */
    public void resetGame() {
        name = "";
        wins = 0;
        losses = 0;
        ties = 0;
        currentRound = 1;
        rockOutCount = 0;
        setChoiceDisabled(false);
        animationPane.getChildren().clear();
        gameOverLabel.setText("");
        updateLabels();
    }

    /*
     * Take in the player's choice, randomly generate the computer's choice and compare to find the winner of a round.
     */
    public int playRound(int playerChoice) {

        Random rand = new Random();
        int computerChoice = rand.nextInt(3);
        double tryToWin = rand.nextDouble();

        //check the chance to win and affect the results
        if (chanceToWin > 0.5) {
            if (tryToWin < chanceToWin) {
                if (playerChoice == 0)
                    computerChoice = 2;
                else if (playerChoice == 1)
                    computerChoice = 0;
                else
                    computerChoice = 1;
            }
        }
        else if (chanceToWin < 0.5) {
            if (tryToWin > chanceToWin) {
                if (playerChoice == 0)
                    computerChoice = 1;
                else if (playerChoice == 1)
                    computerChoice = 2;
                else
                    computerChoice = 0;
            }
        }

        if ((playerChoice == 0 && computerChoice == 1) || (playerChoice == 1 && computerChoice == 2) || (playerChoice == 2 && computerChoice == 0)) {
            losses++;
            gameOverLabel.setText("Computer Wins!");
        }
        else if ((playerChoice == 0 && computerChoice == 2) || (playerChoice == 1 && computerChoice == 0) || (playerChoice == 2 && computerChoice == 1)) {
            wins++;
            gameOverLabel.setText("You Win!");
        }
        else {
            ties++;
            gameOverLabel.setText("It's a Tie!");
        }

        currentRound++;

        return computerChoice;
    }

    /*
     * Disable the ability to click on the rock, paper, scissor options
     */
    public void setChoiceDisabled(Boolean status) {
        rockSelectImage.setDisable(status);
        paperSelectImage.setDisable(status);
        scissorsSelectImage.setDisable(status);
    }

    /*
     * Anchor images inside an AnchorPane in the case that there is a resize of the window.
     */
    public void anchorImages (AnchorPane pane, ImageView rock, ImageView paper, ImageView scissors) {
        AnchorPane.setTopAnchor(rock, 10.0);
        AnchorPane.setRightAnchor(rock, pane.widthProperty().getValue()/2 - rock.getImage().getWidth()/2);
        AnchorPane.setTopAnchor(paper, rock.getImage().getHeight());
        AnchorPane.setLeftAnchor(paper, pane.widthProperty().getValue()/2 + paper.getImage().getWidth()/2);
        AnchorPane.setTopAnchor(scissors, rock.getImage().getHeight());
        AnchorPane.setRightAnchor(scissors, pane.widthProperty().getValue()/2 + scissors.getImage().getWidth()/2);
    }

    /*
     * Make and return a radiobutton using a given name.
     */
    public RadioButton makeRadioButton(String radioName, double fontSize) {
        RadioButton radioButton = new RadioButton(radioName);
        radioButton.setFont(Font.font(DEFAULT_FONT, FontWeight.BOLD, fontSize));
        return radioButton;
    }

    /*
     * Make and return a button using a given name and font size.
     */
    public ToggleButton makeButton(String buttonName, double fontSize) {
        ToggleButton button = new ToggleButton(buttonName);
        button.setFont(Font.font(DEFAULT_FONT, FontWeight.BOLD, fontSize));
        button.setMaxWidth(Double.MAX_VALUE);
        return button;
    }

    /*
     * Make and return a VBox given a set of Nodes.
     */
    public VBox makeVBox(Node... nodes) {
        VBox box = new VBox(nodes);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(15));
        box.setSpacing(5);
        return box;
    }

    /*
     * Make and return an HBox with a given spacing, alignment, and Nodes.
     */
    public HBox makeHBox(int spacing, Pos alignment, Node... nodes) {
        HBox box = new HBox(nodes);
        box.setAlignment(alignment);
        box.setSpacing(spacing);
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    /*
     * Make and return a Label with a given name and font size.
     */
    public Label makeLabel(String text, double fontSize) {
        Label label = new Label(text);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setFont(Font.font(DEFAULT_FONT, FontWeight.BOLD, fontSize));
        return label;
    }

    /*
     * Return an image based on whether rock (0), paper (1), or scissors (2) is needed.
     */
    public Image getAnimationImage(int choice) {
        Image animationImage = null;
        switch (choice) {
            case 0 -> animationImage = rockImageColor;
            case 1 -> animationImage = paperImageColor;
            case 2 -> animationImage = scissorsImageColor;
        }

        return animationImage;
    }

    /*
     * Method checks to see if the game should be over and freezes the game if yes.
     */
    public void checkGameOver() {
        if (currentRound > roundsToPlay) {
            setChoiceDisabled(true);
            gameOverLabel.setText("GAME OVER");
            gameOverLabel.setVisible(true);
        }
        else
            setChoiceDisabled(false);
    }

    /*
     * Play a round of rock, paper, scissors from player's choice
     */
    public void playGame(int playerChoice) {

        //Clear out the animation pane of the previous animation
        animationPane.getChildren().clear();

        // Prepare ImageViews to hold images for the player and computer animations
        ImageView player = new ImageView();
        player.setRotate(90);
        ImageView computer = new ImageView();
        computer.setRotate(-90);

        setChoiceDisabled(true);

        // Hide the win status label until the round is over
        gameOverLabel.setVisible(false);

        int computerChoice = playRound(playerChoice);

        // Get the animation images based on what the player and computer chose
        player.setImage(getAnimationImage(playerChoice));
        computer.setImage(getAnimationImage(computerChoice));

        // Prepare the animation paths
        Line leftLine = new Line(0, animationPane.getPrefHeight()/2, animationPane.getPrefWidth()/2 - player.getImage().getHeight()/2, animationPane.getPrefHeight()/2);
        leftLine.setStroke(Color.TRANSPARENT);
        Line rightLine = new Line(animationPane.getPrefWidth(), animationPane.getPrefHeight()/2, animationPane.getPrefWidth()/2 + computer.getImage().getHeight()/2, animationPane.getPrefHeight()/2);
        rightLine.setStroke(Color.TRANSPARENT);

        animationPane.getChildren().addAll(leftLine, rightLine, player, computer);

        // Prepare the player animation
        PathTransition ptPlayer = new PathTransition();
        ptPlayer.setDuration(Duration.millis(2000));
        ptPlayer.setPath(leftLine);
        ptPlayer.setNode(player);
        ptPlayer.setCycleCount(1);
        ptPlayer.setOnFinished(e -> {
            updateLabels();
            gameOverLabel.setVisible(true);
            checkGameOver();
        });

        // Prepare the computer animation
        PathTransition ptComputer = new PathTransition();
        ptComputer.setDuration(Duration.millis(2000));
        ptComputer.setPath(rightLine);
        ptComputer.setNode(computer);
        ptComputer.setCycleCount(1);

        ptComputer.play();
        ptPlayer.play();
    }

    /*
     * Update the data-related labels as changes are made.
     */
    public void updateLabels() {
        welcomeLabel.setText(String.format("Select an icon below, %s.", name));
        roundsLabel.setText(String.format("ROUND\n%d", currentRound));
        winLabel.setText("WINS\n" + wins);
        lossLabel.setText("LOSSES\n" + losses);
        tieLabel.setText("TIES\n" + ties);
    }

    /*
     * Change the colors and fonts of all GUI elements with a given color palette which is an array of Colors.
     */
    public void loadTheme(String[] palette) {

        changeStyle(palette[1], themeLabel);
        changeStyle(palette[0], metalButton, brightButton, easterButton);
        changeBackground(palette[3], nameField, roundsField);
        changeButtonTheme(palette[3], metalButton, brightButton, easterButton, resetButton, menuButton, dataButton, playButton);
        changeStyle(palette[3], difficultyLabel, welcomeLabel, roundsLabel, winLabel, lossLabel, tieLabel, rockOutLabel);
        changeStyle(palette[2], gameOverLabel);
        changeStyle(palette[2], nameErrorLabel, roundsErrorLabel);
        changeStyle(palette[0], playButton, resetButton, menuButton, dataButton);
        changeStyle(palette[1], babyEasyButton, easyButton, normalButton, hardButton, superHardButton);
        changeBackground(palette[0], mainMenu, gameBox, rockOutBox, chartPane);
    }

    /*
     * Method that changes the text color of a series of given nodes.
     */
    public void changeStyle(String color, Labeled...nodes) {
        for (Labeled node : nodes) {
            node.setTextFill(Color.web(color));
        }
    }

    /*
     * Method that changes the base color of a ToggleButton.
     */
    public void changeButtonTheme(String color, ToggleButton...buttons) {
        for (ToggleButton button : buttons)
            button.setStyle(String.format("-fx-base: %s", color));
    }

    /*
     * Method that changes the background color of a node
     */
    public void changeBackground(String color, Region...nodes) {
        for (Region node : nodes)
            node.setBackground(new Background(new BackgroundFill(Color.web(color), CornerRadii.EMPTY, new Insets(0))));
    }

    /*
     * Make and return a TextField with a given prompt text and font size.
     */
    public TextField makeTextField(String text) {
        TextField field = new TextField();
        field.setPromptText(text);
        field.setMaxWidth(Double.MAX_VALUE);

        return field;
    }

    public static void main(String[] args) {
        launch();
    }
}