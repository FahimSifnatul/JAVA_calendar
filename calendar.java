/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class calendar{
    static JLabel lblMonth, lblYear, event_label;
    static JTextField event_details;
    static JButton btnPrev, btnNext, add_event;
    static JTable tblCalendar;
    static JComboBox cmbYear, event_year, event_month, event_day;
    static JFrame frmMain;
    static Container pane;
    static DefaultTableModel mtblCalendar; //Table model
    static JScrollPane stblCalendar; //The scrollpane
    static JPanel pnlCalendar;
    static int realYear, realMonth, realDay, currentYear, currentMonth;
    static HashMap<String,String>event_dict = new HashMap<String,String>();  
    
    public static void main (String args[]){
        //Look and feel
        try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}
        catch (ClassNotFoundException e) {}
        catch (InstantiationException e) {}
        catch (IllegalAccessException e) {}
        catch (UnsupportedLookAndFeelException e) {}
        
        //Prepare frame
        frmMain = new JFrame ("JAVA Calendar"); //Create frame
        frmMain.setSize(530, 425); //Set size to 400x400 pixels
        pane = frmMain.getContentPane(); //Get content pane
        pane.setLayout(null); //Apply null layout
        frmMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Close when X is clicked
        
        //Create controls
        lblMonth = new JLabel ("January");
        lblYear  = new JLabel ("Change year:");
        event_label = new JLabel("Set a new Event:");
        cmbYear  = new JComboBox();
        btnPrev  = new JButton ("<");
        btnNext  = new JButton (">");
        mtblCalendar = new DefaultTableModel(){public boolean isCellEditable(int rowIndex, int mColIndex){return false;}};
        tblCalendar = new JTable(mtblCalendar);
        stblCalendar = new JScrollPane(tblCalendar);
        pnlCalendar = new JPanel(null);
        //Create events
        add_event     = new JButton("SET");
        event_day     = new JComboBox();
        event_month   = new JComboBox();
        event_year    = new JComboBox();
        event_details = new JTextField("New Event");
        
        //Set border
        pnlCalendar.setBorder(BorderFactory.createTitledBorder("Calendar"));
        
        //Register action listeners
        btnPrev.addActionListener(new btnPrev_Action());
        btnNext.addActionListener(new btnNext_Action());
        cmbYear.addActionListener(new cmbYear_Action());
        //Event Register
        add_event.addActionListener(new add_event_Action());
        
        //Add controls to pane
        pane.add(pnlCalendar);
        //add to pane
        pnlCalendar.add(lblMonth);
        pnlCalendar.add(lblYear);
        pnlCalendar.add(cmbYear);
        pnlCalendar.add(btnPrev);
        pnlCalendar.add(btnNext);
        pnlCalendar.add(stblCalendar);
        //Event addition to pane
        pnlCalendar.add(event_label);
        pnlCalendar.add(event_day);
        pnlCalendar.add(event_month);
        pnlCalendar.add(event_year);
        pnlCalendar.add(add_event);
        pnlCalendar.add(event_details);
        
        //Set bounds
        pnlCalendar.setBounds(0, 0, 520, 390);
        lblMonth.setBounds(252-lblMonth.getPreferredSize().width/2, 25, 100, 25);
        lblYear.setBounds(10, 305, 80, 20);
        cmbYear.setBounds(130, 305, 50, 20);
        btnPrev.setBounds(10, 25, 50, 25);
        btnNext.setBounds(460, 25, 50, 25);
        stblCalendar.setBounds(10, 50, 500, 250);
        //Event bounds
        event_label.setBounds(10, 330, 110, 20);
        event_day.setBounds(10, 355, 50, 20);
        event_month.setBounds(70, 355, 50, 20);
        event_year.setBounds(130, 355, 50, 20);
        event_details.setBounds(190, 355, 240, 20);
        add_event.setBounds(440, 355, 70, 20);
        
        //Make frame visible
        frmMain.setResizable(false);
        frmMain.setVisible(true);
        
        //Get real month/year
        GregorianCalendar cal = new GregorianCalendar(); //Create calendar
        realDay = cal.get(GregorianCalendar.DAY_OF_MONTH); //Get day
        realMonth = cal.get(GregorianCalendar.MONTH); //Get month
        realYear = cal.get(GregorianCalendar.YEAR); //Get year
        currentMonth = realMonth; //Match month and year
        currentYear = realYear;
        
        //Add headers
        String[] headers = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"}; //All headers
        for (int i=0; i<7; i++){
            mtblCalendar.addColumn(headers[i]);
        }
        
        tblCalendar.getParent().setBackground(tblCalendar.getBackground()); //Set background
        
        //No resize/reorder
        tblCalendar.getTableHeader().setResizingAllowed(false);
        tblCalendar.getTableHeader().setReorderingAllowed(false);
        
        //Single cell selection
        tblCalendar.setColumnSelectionAllowed(true);
        tblCalendar.setRowSelectionAllowed(true);
        tblCalendar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        //Set row/column count
        tblCalendar.setRowHeight(38);
        mtblCalendar.setColumnCount(7);
        mtblCalendar.setRowCount(6);
        
        //Populate table
        for (int i=realYear-100; i<=realYear+100; i++){
            cmbYear.addItem(Integer.toString(i));
        }
        //Add items to event combobox
        for (int i=1; i<=31; i++){
            event_day.addItem(Integer.toString(i));
        }
        for (int i=1; i<=12; i++){
            event_month.addItem(Integer.toString(i));
        }
        for (int i=realYear-100; i<=realYear+100; i++){
            event_year.addItem(Integer.toString(i));
        }
        
        //Refresh calendar
        refreshCalendar (realMonth, realYear); //Refresh calendar
    }
    
    public static void refreshCalendar(int month, int year){
        //Variables
        String[] months =  {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        int nod, som; //Number Of Days, Start Of Month
        
        //Allow/disallow buttons
        btnPrev.setEnabled(true);
        btnNext.setEnabled(true);
        if (month == 0 && year <= realYear-100){btnPrev.setEnabled(false);} //Too early
        if (month == 11 && year >= realYear+100){btnNext.setEnabled(false);} //Too late
        lblMonth.setText(months[month]); //Refresh the month label (at the top)
        lblMonth.setBounds(252-lblMonth.getPreferredSize().width/2, 25, 180, 25); //Re-align label with calendar
        cmbYear.setSelectedItem(String.valueOf(year)); //Select the correct year in the combo box
        //event 
        event_details.setText("New Event");
        event_month.setSelectedItem(String.valueOf(month+1)); //Select the correct month in the combo box
        event_year.setSelectedItem(String.valueOf(year)); //Select the correct year in the combo box     
        
        //Clear table
        for (int i=0; i<6; i++){
            for (int j=0; j<7; j++){
                mtblCalendar.setValueAt(null, i, j);
            }
        }
        
        //remove all days from day comboBox
        event_day.removeAllItems();
        
        //Get first day of month and number of days
        GregorianCalendar cal = new GregorianCalendar(year, month, 1);
        nod = cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
        som = cal.get(GregorianCalendar.DAY_OF_WEEK);
        
        //Draw calendar
        for (int i=1; i<=nod; i++){
            int row = new Integer((i+som-2)/7);
            int column  =  (i+som-2)%7;
            mtblCalendar.setValueAt(i, row, column);
            event_day.addItem(Integer.toString(i));
        }
        
        //Apply renderers
        tblCalendar.setDefaultRenderer(tblCalendar.getColumnClass(0), new tblCalendarRenderer());
    }
    
    static class tblCalendarRenderer extends DefaultTableCellRenderer{
        @Override
        public Component getTableCellRendererComponent (JTable table, Object value, boolean selected, boolean focused, int row, int column){
            super.getTableCellRendererComponent(table, value, selected, focused, row, column);
            if (column == 5 || column == 6){ //Week-end
                setBackground(new Color(255, 220, 220));
            }
            else{ //Week
                setBackground(new Color(255, 255, 255));
            }
            if (value != null){
                String event_date = value.toString() + "-" + 
                                    Integer.toString(currentMonth+1) + "-" +
                                    Integer.toString(currentYear);
                if (Integer.parseInt(value.toString()) == realDay && currentMonth == realMonth && currentYear == realYear){ //Today
                    setBackground(new Color(220, 220, 255));
                }
                else if(event_dict.containsKey(event_date) == true){
                    setBackground(new Color(175, 255, 175));
                    setToolTipText(event_dict.get(event_date));
                }
                else{
                    setToolTipText(null);
                }
            }
            setBorder(null);
            setForeground(Color.black);
            return this;
        }
    }
    
    static class btnPrev_Action implements ActionListener{
        @Override
        public void actionPerformed (ActionEvent e){
            if (currentMonth == 0){ //Back one year
                currentMonth = 11;
                currentYear -= 1;
            }
            else{ //Back one month
                currentMonth -= 1;
            }
            refreshCalendar(currentMonth, currentYear);
        }
    }
    static class btnNext_Action implements ActionListener{
        @Override
        public void actionPerformed (ActionEvent e){
            if (currentMonth == 11){ //Foward one year
                currentMonth = 0;
                currentYear += 1;
            }
            else{ //Foward one month
                currentMonth += 1;
            }
            refreshCalendar(currentMonth, currentYear);
        }
    }
    static class cmbYear_Action implements ActionListener{
        @Override
        public void actionPerformed (ActionEvent e){
            if (cmbYear.getSelectedItem() != null){
                String b = cmbYear.getSelectedItem().toString();
                currentYear = Integer.parseInt(b);
                refreshCalendar(currentMonth, currentYear);
            }
        }
    }
    //event ActionListener
    static class add_event_Action implements ActionListener{
        @Override
        public void actionPerformed (ActionEvent e){
            String day, month, year;
            day   = event_day.getSelectedItem().toString();
            month = event_month.getSelectedItem().toString();
            year  = event_year.getSelectedItem().toString();           
            event_dict.put(day + "-" + month + "-" + year, event_details.getText() );
            
            currentMonth = Integer.parseInt(month)-1;
            currentYear  = Integer.parseInt(year);
            refreshCalendar(currentMonth, currentYear);
        }
    }
}