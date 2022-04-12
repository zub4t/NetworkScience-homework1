package models;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.event.*;
import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.awt.*;
import nodes.Node;
import bfs.BFS;

import java.util.List;
import java.util.Random;
import java.util.Set;

class Pair {
 
    // Pair attributes
    public int x;
    public int y;
 
    // Constructor to initialize pair
    public Pair(int x, int y)
    {
        // This keyword refers to current instance
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Pair){
            return (this.x == ((Pair) obj).x && this.y == ((Pair) obj).y) || (this.x == ((Pair) obj).y && this.y == ((Pair) obj).x);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.x * this.y;
    }
}

public class App {
    static double prob = Integer.MAX_VALUE;
    static int maxNumberOfConnections = 2;

    public static void main(String[] args) {
        final Canvas canvas = new Canvas();
        canvas.setSize(1000, 800);
        final JFrame frame = new JFrame("Modelo Erdős–Rényi");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1050, 850);

        final JPanel panel1 = new JPanel();
        final JLabel labelNodes = new JLabel("number of nodes");
        final JLabel nodesSliderValue = new JLabel("10");

        final JSlider nodesSlider = new JSlider(JSlider.HORIZONTAL, 0, 2000, 10);
        nodesSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                nodesSliderValue.setText(nodesSlider.getValue() + "");

            }

        });
        panel1.add(labelNodes);
        panel1.add(nodesSlider);

        final JLabel labelProbability = new JLabel("probability to connect");
        final JLabel probabilitySliderValue = new JLabel("0%");
        final JSlider probabilitySlider = new JSlider(JSlider.HORIZONTAL, 0, Integer.MAX_VALUE, 0);
        probabilitySlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                probabilitySliderValue.setText((probabilitySlider.getValue() / App.prob) + "%");

            }

        });
        final JPanel panel2 = new JPanel();

        panel2.add(labelProbability);
        panel2.add(probabilitySlider);

        final JPanel panel = new JPanel();

        final JPanel panel11 = new JPanel();
        panel11.add(BorderLayout.NORTH, panel1);
        panel11.add(BorderLayout.CENTER, nodesSliderValue);

        final JPanel panel22 = new JPanel();
        panel11.add(BorderLayout.NORTH, panel2);
        panel11.add(BorderLayout.CENTER, probabilitySliderValue);

        panel.add(panel11);
        panel.add(panel22);

        JButton drawErdosrenyi = new JButton("draw erdosrenyi");
        drawErdosrenyi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                erdosrenyi(canvas, nodesSlider, probabilitySlider);
            }
        });

        JButton drawBarabasi = new JButton("draw barabasi");
        drawBarabasi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                barabasi(canvas, nodesSlider, probabilitySlider);
            }
        });
        // Adding Components to the frame.
        frame.getContentPane().add(BorderLayout.NORTH, panel);
        frame.getContentPane().add(BorderLayout.CENTER, canvas);

        final JPanel panelModel = new JPanel();
        panelModel.add(drawErdosrenyi);
        panelModel.add(drawBarabasi);
        frame.getContentPane().add(BorderLayout.SOUTH, panelModel);

        frame.setVisible(true);

    }

    public static void erdosrenyi(Canvas canvas, JSlider nodesSlider, JSlider probabilitySlider) {

        Graphics g = canvas.getGraphics();
        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        List<Node> list = new ArrayList<>();
        for (int i = 0; i < nodesSlider.getValue(); i++) {
            list.add(Node.builder()
                    .x((int) Math.round(Math.random() * 1000))
                    .y((int) Math.round(Math.random() * 800))
                    .ID(i)
                    .connectedTo(new ArrayList<Node>()).build());

        }

        double probSliderValue = probabilitySlider.getValue() / App.prob;
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {

                Random r = new java.util.Random();
                double prob = r.nextDouble();
                if (prob <= probSliderValue) {
                    list.get(i).getConnectedTo().add(list.get(j));
                    list.get(j).getConnectedTo().add(list.get(i));
                }
            }
        }
        HashSet<Pair> connections = new HashSet<>();
        System.out.println(list.size());
        for(Node node : list){
            for(Node connected : node.getConnectedTo()){
                connections.add(new Pair(node.getID(), connected.getID()));
            }
        }

        for(Pair p : connections){
            System.out.println(p.x + " " + p.y);
        }

        for (Node node : list) {
            g.setColor(new Color(
                    (int) Math.random() * 255,
                    (int) Math.random() * 100,
                    (int) Math.random() * 100));
            g.drawArc(
                    node.getX(),
                    node.getY(),
                    5, 5, 0, 360);

            g.drawString(node.getID() + "", node.getX(), node.getY());
            for (Node connectedToNode : node.getConnectedTo()) {
                g.drawLine(node.getX(),
                        node.getY(),
                        connectedToNode.getX(),
                        connectedToNode.getY());
            }
        }
        List<Set<Node>> setList = BFS.doBFS(list);
        Set<Node> giantComponent = null;
        int max = -1;
        for (Set<Node> set : setList) {
            if (max < set.size()) {
                giantComponent = set;
                max = set.size();
            }
        }

        if (max > 0) {

            Iterator<Node> itr = giantComponent.iterator();
            while (itr.hasNext()) {
                Node nnn = itr.next();
                g.setColor(new Color(255, 0, 0));
                g.fillArc(
                        nnn.getX(),
                        nnn.getY(),
                        5, 5, 0, 360);
            }

        }
    }

    public static void barabasi(Canvas canvas, JSlider nodesSlider, JSlider probabilitySlider) {

        final Graphics g = canvas.getGraphics();
        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        final List<Node> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            list.add(Node.builder()
                    .x((int) Math.round(Math.random() * 1000))
                    .y((int) Math.round(Math.random() * 800))
                    .ID(i)
                    .degree(0)
                    .connectedTo(new ArrayList<Node>()).build());

        }

        for (int i = 0; i < list.size(); i++) {
            List<Node> auxList = new ArrayList<>();
            auxList.addAll(list);
            while (list.size() - auxList.size() < App.maxNumberOfConnections) {
                int current = (int) Math.round(Math.random() * (auxList.size() - 1));
                if (current != i) {
                    Node nn = auxList.get(current);
                    Random r = new java.util.Random();
                    double prob = r.nextDouble();
                    if (prob <= 0.5) {
                        list.get(i).getConnectedTo().add(list.get(nn.getID()));
                        list.get(nn.getID()).getConnectedTo().add(list.get(i));

                        list.get(i).setDegree(list.get(i).getDegree() + 1);
                        list.get(nn.getID()).setDegree(list.get(nn.getID()).getDegree() + 1);

                        auxList.remove(current);
                    }
                }

            }

        }

        for (Node node : list) {
            g.setColor(new Color(
                    (int) Math.random() * 255,
                    (int) Math.random() * 100,
                    (int) Math.random() * 100));
            g.drawArc(
                    node.getX(),
                    node.getY(),
                    5, 5, 0, 360);

            g.drawString(node.getID() + "", node.getX(), node.getY());
            for (Node connectedToNode : node.getConnectedTo()) {
                g.drawLine(node.getX(),
                        node.getY(),
                        connectedToNode.getX(),
                        connectedToNode.getY());
            }
        }

        new Thread() {
            public void run() {
                while (true) {
                    Node currentNode = Node.builder()
                            .x((int) Math.round(Math.random() * 1000))
                            .y((int) Math.round(Math.random() * 800))
                            .ID(list.size())
                            .connectedTo(new ArrayList<Node>())
                            .build();
                    while (currentNode.getConnectedTo().size() < App.maxNumberOfConnections) {
                        int current = (int) Math.round(Math.random() * (list.size() - 1));
                        Node nn = list.get(current);
                        if (Math.random() <= (double) nn.getDegree() / list.size() * 2) {
                            currentNode.getConnectedTo().add(list.get(nn.getID()));

                            nn.getConnectedTo().add(currentNode);
                            nn.setDegree(nn.getDegree() + 1);
                        }
                    }

                    list.add(currentNode);
                    currentNode.setDegree(2);
                    for (Node node : list) {
                        g.setColor(new Color(
                                (int) Math.random() * 255,
                                (int) Math.random() * 100,
                                (int) Math.random() * 100));
                        g.drawArc(
                                node.getX(),
                                node.getY(),
                                5, 5, 0, 360);

                        g.drawString(node.getID() + "", node.getX(), node.getY());
                        for (Node connectedToNode : node.getConnectedTo()) {
                            g.drawLine(node.getX(),
                                    node.getY(),
                                    connectedToNode.getX(),
                                    connectedToNode.getY());
                        }
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();

    }

}
