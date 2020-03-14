package com.walle;

import com.walle.audio.ChatUtil;
import com.walle.audio.RecordHelper;
import com.walle.audio.TimeListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatFrame {
    private static final long MS_DURATION = 5000;
    private static final String RECORD = "      请讲话";
    private static final String PLAY = "      --------";
    private static boolean isChatting = false;

    private static JButton recordBtn;
    private static JLabel recordLbl;
    private static TimeListener playerListener;
    private static TimeListener recorderListener;

    static {
        recordBtn = new JButton("开始聊天");
        recordLbl = new JLabel(PLAY);

        recordBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (ChatFrame.class) {
                    isChatting = !isChatting;
                    recordBtn.setText(isChatting ? "结束聊天" : "开始聊天");

                    RecordHelper recordHelper = RecordHelper.getInst();
                    if (isChatting) {
                        recordHelper.record(recorderListener, MS_DURATION);
                    } else {
                        recordHelper.stop();
                    }
                }
            }
        });

        recorderListener = new TimeListener() {
            @Override
            public void timeUpdated(long seconds) {
                recordLbl.setText(String.format("%s(%d)", RECORD, MS_DURATION / 1000 - seconds));
            }

            @Override
            public void stopped(long seconds) {
                recordLbl.setText(PLAY);
                synchronized (ChatFrame.class) {
                    if (isChatting) {
                        ChatUtil.chat(playerListener);
                    }
                }
            }
        };

        playerListener = new TimeListener() {
            @Override
            public void timeUpdated(long seconds) {
            }

            @Override
            public void stopped(long seconds) {
                synchronized (ChatFrame.class) {
                    if (isChatting) {
                        recordLbl.setText(String.format("%s(%d)", RECORD, MS_DURATION / 1000));
                        RecordHelper recordHelper = RecordHelper.getInst();
                        recordHelper.record(recorderListener, MS_DURATION);
                    }
                }
            }
        };
    }

    public static JFrame showFrame() {
        // create frame
        final JFrame frame = new JFrame("aiChat - 智能语音聊天机器人");
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);

        Box chatBox = Box.createVerticalBox();
        chatBox.add(recordBtn);
        chatBox.add(recordLbl);

        // create panel
        JPanel panel = new JPanel();
        panel.add(Box.createVerticalStrut(150));
        panel.add(chatBox);

        // show panel
        frame.setContentPane(panel);
        frame.setVisible(true);

        // do work
        frame.getRootPane().setDefaultButton(recordBtn);
        recordBtn.doClick();

        return frame;
    }
}
