package adoctor.presentation.dialog;






import adoctor.application.resultsFromFile.PieChartPanel;
import adoctor.application.resultsFromFile.SmellResult;
import adoctor.application.resultsFromFile.SmellResultList;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ResultsFromFileDialog extends AbstractDialog{
    private static final String TITLE = "aDoctor - Results";

    private JButton backButton;
    private JButton refactorButton;
    private JButton selectAllButton;
    private JButton deselectAllButton;
    private JScrollPane smellScrollPanel;
    private JScrollPane classScrollPanel;
    private JPanel pieChartPanel;
    private JPanel contentPane;
    private JLabel instacesNumber;
    private JPanel smellPanel;
    private JPanel classesPanel;
    private PieChartPanel pcp;

    private ResultsFromFileCallback resultsCallback;
    private SmellResultList smells;




    private ResultsFromFileDialog(ResultsFromFileCallback resultsCallback, SmellResultList smells)  {
        this.resultsCallback = resultsCallback;
        this.smells=smells;
        init();

    }

    public static void show(ResultsFromFileCallback resultsCallback, SmellResultList smells){
        ResultsFromFileDialog resultsFromFileDialog = new ResultsFromFileDialog(resultsCallback, smells);
        resultsFromFileDialog.showInCenter();
    }

    private void init()  {

        super.init(contentPane, TITLE, refactorButton);

        backButton.addActionListener(e -> onBack());
        refactorButton.addActionListener(e -> onRefactor());
        selectAllButton.addActionListener(e->onSelectAll());
        deselectAllButton.addActionListener(e->onDeselectAll());
        instacesNumber.setText("Instances Found: " + this.smells.getSmellResultList().size());


        String[] labels= new String[smells.getSmellTypes().size()];
        double[] percentages = new double[smells.getSmellTypes().size()];
        for (int i=0; i<smells.getSmellTypes().size(); i++){

            labels[i] = smells.getSmellTypes().get(i);
            double inst = smells.instancesNumberOfType(labels[i]);
            double total = smells.getSmellResultList().size();
            double perc = (inst/total)*100;
            percentages[i] = perc+(1/smells.getSmellTypes().size());//+1 to remove the 1/360 gap
            System.out.println(percentages[i]+" : "+1/smells.getSmellTypes().size());
        }


        //setting the pie chart
        PieChartPanel pcp=new PieChartPanel(labels, percentages);
        pieChartPanel.setLayout(new GridLayout());
        pieChartPanel.add(pcp);
        Dimension newDim = new Dimension(300, 100);
        pieChartPanel.setPreferredSize(newDim);
        pieChartPanel.setSize(newDim);
        pieChartPanel.validate();


        //setting the smell panel
        smellPanel.setLayout(new GridLayout(6,1));

        JPanel[] smellPanelComponents=new JPanel[smells.getSmellTypes().size()];
        JButton[] smellButtons=new JButton[smells.getSmellTypes().size()];

        for (int i=0;i<smells.getSmellTypes().size();i++){
            smellPanelComponents[i] = new JPanel(new GridLayout(1,1));
            smellButtons[i] = new JButton(smells.getSmellTypes().get(i));

            int finalI = i;
            smellButtons[i].addActionListener(e -> onClickSmellButton(smellButtons[finalI].getText()));

            smellPanelComponents[i].add(smellButtons[i]);
            smellPanel.add(smellPanelComponents[i]);
        }

        smellPanel.validate();


        //setting the java classes panel
        //onClickSmellButton(smellButtons[0].getText());


        //setting the window size
        Dimension contentPaneDimension = new Dimension(490, 630);
        contentPane.setPreferredSize(contentPaneDimension);
        contentPane.setSize(contentPaneDimension);
        contentPane.validate();

        onClickSmellButton(smellButtons[0].getText());
    }

    private void onBack(){
        resultsCallback.resultsBack(this);
    }
    private void onRefactor(){
        resultsCallback.resultsRefactor(this, this.smells);
    }

    private void onClickSmellButton(String smellType){
        classesPanel.removeAll();
        ArrayList<SmellResult> smellResultsOfType = smells.getSmellResultsOfType(smellType);
        classesPanel.setLayout(new GridLayout(smellResultsOfType.size(),1));

        JPanel[] classesPanelComponents = new JPanel[smellResultsOfType.size()];
        JCheckBox[] checkBoxes = new JCheckBox[smellResultsOfType.size()];

        for (int i=0;i<smellResultsOfType.size();i++){
            classesPanelComponents[i] = new JPanel(new GridLayout(1,1));
            checkBoxes[i] = new JCheckBox(smellResultsOfType.get(i).getFile().getPath());
            checkBoxes[i].setSelected(smellResultsOfType.get(i).getSelected());
            int finalI = i;
            checkBoxes[i].addActionListener(e-> onClickClassCheckbox(smellResultsOfType.get(finalI), checkBoxes[finalI].isSelected()));

            classesPanelComponents[i].add(checkBoxes[i]);
            classesPanel.add(classesPanelComponents[i]);
        }

        classesPanel.validate();
    }


    private void onClickClassCheckbox(SmellResult smellResult, boolean selected){
        smells.setSelected(smellResult, selected);
    }

    private void onSelectAll(){

        for (int i=0; i<classesPanel.getComponents().length;i++){
            //((JCheckBox)((JPanel)classesPanel.getComponents()[i]).getComponent(0)).isSelected();
            //cb = (JCheckBox) ((JPanel)jc[i]).getComponent(0);
            ((JCheckBox)((JPanel)classesPanel.getComponents()[i]).getComponent(0)).setSelected(true);
            ActionListener actionListener;
            actionListener=((JCheckBox)((JPanel)classesPanel.getComponents()[i]).getComponent(0)).getActionListeners()[0];
            actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));
        }

    }

    private void onDeselectAll(){

        for (int i=0; i<classesPanel.getComponents().length;i++){
            //((JCheckBox)((JPanel)classesPanel.getComponents()[i]).getComponent(0)).isSelected();
            //cb = (JCheckBox) ((JPanel)jc[i]).getComponent(0);
            ((JCheckBox)((JPanel)classesPanel.getComponents()[i]).getComponent(0)).setSelected(false);
            ActionListener actionListener;
            actionListener=((JCheckBox)((JPanel)classesPanel.getComponents()[i]).getComponent(0)).getActionListeners()[0];
            actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));
        }
    }

    private ArrayList<SmellResult> getSelectedFiles(){

        ArrayList<SmellResult> selectetFiles = new ArrayList<>();
        for (int i=0; i<smells.getSmellResultList().size(); i++){
            if(smells.getSmellResultList().get(i).getSelected()){
                selectetFiles.add(smells.getSmellResultList().get(i));
            }
        }

        return selectetFiles;
    }




    public interface ResultsFromFileCallback{
        void resultsBack(ResultsFromFileDialog resultsFromFileDialog);

        void resultsRefactor(ResultsFromFileDialog resultsFromFileDialog, SmellResultList smellResultList);
    }
}
