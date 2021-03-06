package adoctor.presentation.dialog;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SuccessDialog extends AbstractDialog {
    private static final String TITLE = "aDoctor - Success";

    private SuccessCallback successCallback;

    private JPanel contentPane;
    private JButton buttonAnalyze;
    private JButton buttonBack;

    public static void show(SuccessCallback successCallback) {
        SuccessDialog successDialog = new SuccessDialog(successCallback);
        successDialog.showInCenter();
    }

    private SuccessDialog(SuccessCallback successCallback) {
        this.successCallback = successCallback;
        init();
    }

    private void init() {
        super.init(contentPane, TITLE, buttonAnalyze);

        buttonAnalyze.addActionListener(e -> onAnalyze());
        buttonBack.addActionListener(e -> onBack());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onQuit();
            }
        });
    }

    private void onAnalyze() {
        successCallback.successAnalyze(this);
    }

    private void onBack() {
        successCallback.successBack(this);
    }

    private void onQuit() {
        successCallback.successQuit(this);
    }

    public interface SuccessCallback {
        void successAnalyze(SuccessDialog successDialog);

        void successBack(SuccessDialog successDialog);

        void successQuit(SuccessDialog successDialog);
    }
}
