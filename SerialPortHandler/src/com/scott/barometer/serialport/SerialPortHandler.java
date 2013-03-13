/*
 * SerialPortHandler.java
 *
 * Created on __DATE__, __TIME__
 */

package com.scott.barometer.serialport;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.log4j.Logger;

import com.google.common.base.Optional;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * 
 * @author __USER__
 */
public class SerialPortHandler extends javax.swing.JFrame {

	private static ActorRef serialPortProxy;
	private static ActorSystem actorSystem;
	private static ItemListener serialPortItemHandler;
	private static ActorRef messageReceiver;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5064388124794093556L;

	/** Creates new form SerialPortHandler */
	public SerialPortHandler() {
		initComponents();
		serialPortItemHandler = new SerialPortItemHandler();
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				List<String> ports = Utils.getCOMPorts();
				getLogger().info("Found COM Ports: " + ports);
				for (Iterator<String> portsIter = ports.iterator(); portsIter
						.hasNext();) {
					JCheckBoxMenuItem mItem = new JCheckBoxMenuItem(portsIter
							.next());
					mItem.addItemListener(serialPortItemHandler);
					jMenu1.add(mItem);
				}
				jMenu1.repaint();
			}
		});
		actorSystem = ActorSystem.create("MyActors");
		messageReceiver = actorSystem.actorOf(new Props(
				new UntypedActorFactory() {
					private static final long serialVersionUID = 4741780784339054509L;

					@Override
					public Actor create() throws Exception {
						return new MessageReceiver();
					}
				}), "receiver");
		getLogger().debug(
				">>> Created message receiver: " + messageReceiver.path());
	}

	// GEN-BEGIN:initComponents
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		menuBar = new javax.swing.JMenuBar();
		fileMenu = new javax.swing.JMenu();
		exitMenuItem = new javax.swing.JMenuItem();
		editMenu = new javax.swing.JMenu();
		jMenu1 = new javax.swing.JMenu();
		helpMenu = new javax.swing.JMenu();
		contentsMenuItem = new javax.swing.JMenuItem();
		aboutMenuItem = new javax.swing.JMenuItem();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				formWindowClosing(evt);
			}
		});

		fileMenu.setText("File");

		exitMenuItem.setText("Exit");
		exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				exitMenuItemActionPerformed(evt);
			}
		});
		fileMenu.add(exitMenuItem);

		menuBar.add(fileMenu);

		editMenu.setText("Tools");

		jMenu1.setText("SerialPorts");
		editMenu.add(jMenu1);

		menuBar.add(editMenu);

		helpMenu.setText("Help");

		contentsMenuItem.setText("Contents");
		helpMenu.add(contentsMenuItem);

		aboutMenuItem.setText("About");
		helpMenu.add(aboutMenuItem);

		menuBar.add(helpMenu);

		setJMenuBar(menuBar);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 400,
				Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 279,
				Short.MAX_VALUE));

		pack();
	}// </editor-fold>
		// GEN-END:initComponents

	private void formWindowClosing(java.awt.event.WindowEvent evt) {
		exitMenuItemActionPerformed(new ActionEvent(evt, evt.getID(),
				evt.paramString()));
	}

	private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_exitMenuItemActionPerformed
		if (Optional.fromNullable(serialPortProxy).isPresent()) {
			actorSystem.stop(serialPortProxy);
		}
		actorSystem.shutdown();
		System.exit(0);
	}// GEN-LAST:event_exitMenuItemActionPerformed

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			getLogger().warn(e);
			try {
				UIManager.setLookAndFeel(UIManager
						.getCrossPlatformLookAndFeelClassName());
			} catch (Exception ex) {
				getLogger().warn(ex);
			}
		}
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new SerialPortHandler().setVisible(true);
			}
		});
	}

	private static Logger getLogger() {
		return Logger.getLogger(SerialPortHandler.class);
	}

	private static class SerialPortItemHandler implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getItem();
				final String portId = item.getText();
				serialPortProxy = actorSystem.actorOf(new Props(
						new UntypedActorFactory() {

							private static final long serialVersionUID = 670945109736850317L;

							@Override
							public Actor create() throws Exception {
								return new SerialPortBroker(portId);
							}
						}), "serialPortProxy");
				getLogger().debug(serialPortProxy.path());
			} else {
				actorSystem.stop(serialPortProxy);
			}

		}

	}

	private static class MessageReceiver extends UntypedActor {

		LoggingAdapter log = Logging.getLogger(getContext().system(), this);

		@Override
		public void onReceive(Object message) throws Exception {
			log.debug(message.toString());
		}

	}

	// GEN-BEGIN:variables
	// Variables declaration - do not modify
	private javax.swing.JMenuItem aboutMenuItem;
	private javax.swing.JMenuItem contentsMenuItem;
	private javax.swing.JMenu editMenu;
	private javax.swing.JMenuItem exitMenuItem;
	private javax.swing.JMenu fileMenu;
	private javax.swing.JMenu helpMenu;
	private javax.swing.JMenu jMenu1;
	private javax.swing.JMenuBar menuBar;
	// End of variables declaration//GEN-END:variables

}