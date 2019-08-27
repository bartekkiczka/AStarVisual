import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class VisualFrame extends JPanel implements Runnable {

    public static final int RECT = 99;

    public static Graphics2D g2d;

    ExecutorService executor;
    private Thread mainThread;
    private boolean running;

    public static List<Node> path;
    Node initialNode = new Node(1,1);
    Node finalNode = new Node(6,12);
    List<Node> node = null;
    int rows = 20;
    int cols = 14;
    Astar aStar = new Astar(rows,cols,initialNode,finalNode);
    int[][] blocksArray = new int[][]{{1,3}, {2,3}, {3,3}, {4,3}, {5,3}, {6,3}, {7,3}, {8,3}, {9,3}};
    List<Pair<Integer,Integer>> blockadeArray = new ArrayList<>();

    private JFrame frame;
    private boolean[] checkMove = new boolean[8];

    public VisualFrame(){

        executor = Executors.newSingleThreadExecutor();

        for(int i=0; i<blocksArray.length; i++){
            for(int j=0; j<1; j++){
                blockadeArray.add(new Pair<Integer,Integer>(blocksArray[i][j],blocksArray[i][j+1]));
            }
        }

        aStar.setBlocks(blocksArray);
        path = aStar.findPath();

        frame = new JFrame();
        frame.setSize(new Dimension(1920,1080));
        frame.setContentPane(this);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setVisible(true);


        this.revalidate();
        this.repaint();
    }

    @Override
    public void run() {

    }

    @Override
    public void paintComponent(Graphics g) {

        g2d = (Graphics2D) g;
        g.setColor(Color.green);
        g.fillRect(initialNode.getCol() * RECT, initialNode.getRow() * RECT, RECT, RECT);

        g.setColor(Color.blue);
        g.fillRect(finalNode.getCol() * RECT, finalNode.getRow() * RECT, RECT, RECT);

        g.setColor(Color.gray);
        for (int i = 0; i < blockadeArray.size(); i++) {
            g.fillRect(blockadeArray.get(i).getValue() * RECT, blockadeArray.get(i).getKey() * RECT, RECT, RECT);
        }

        g.setColor(Color.black);
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                g.drawRect(i * RECT, j * RECT, RECT, RECT);
            }
        }

        executor.submit(() -> {
            Thread renderThread = new Thread(new RenderThread(g));
            renderThread.start();
        });

    }

    class RenderThread implements Runnable{

        private Graphics g;

        public RenderThread(Graphics g){
            this.g = g;
        }

        @Override
        public void run() {

            g.setColor(Color.yellow);
            System.out.println("THREAD IS WORKING");
            for (int i = 1; i < VisualFrame.path.size() - 1; i++) {
                System.out.println(i);
                g.fillRect(VisualFrame.path.get(i).getCol() * VisualFrame.RECT, VisualFrame.path.get(i).getRow() * VisualFrame.RECT, VisualFrame.RECT, VisualFrame.RECT);
                try {
                    Thread.currentThread().sleep(300);
                    System.out.println("THREAD IS SLEEPING");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("THREAD IS AWAKE");
            }
        }
    }
}
