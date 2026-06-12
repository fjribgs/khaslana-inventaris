/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package khaslanainventaris;

import java.awt.Color;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author rafidhp
 */
public class MainMenu extends javax.swing.JFrame {
    connection dbConn;
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainMenu.class.getName());
    
    private void styleTable() {
        // BODY
        dataTable.setRowHeight(25);
        dataTable.setGridColor(new Color(153, 255, 51));

        // HEADER FIX
        dataTable.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    javax.swing.JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                javax.swing.JLabel label = new javax.swing.JLabel(value.toString());
                label.setOpaque(true);
                label.setBackground(new Color(153, 255, 51));
                label.setForeground(new Color(27, 30, 38));
                label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                label.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));

                return label;
            }
        });

        // BODY RENDERER
        dataTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    javax.swing.JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                javax.swing.JLabel cell = (javax.swing.JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                cell.setBackground(new Color(27, 30, 38));
                cell.setForeground(Color.WHITE);

                if (isSelected) {
                    cell.setBackground(new Color(153, 255, 51));
                    cell.setForeground(new Color(27, 30, 38));
                }

                return cell;
            }
        });

        // FIX AREA KOSONG
        jScrollPane1.getViewport().setBackground(new Color(27, 30, 38));

        // BORDER
        jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(153, 255, 51)));
    }
    
    private void styleButton(javax.swing.JButton btn) {
        btn.setFocusPainted(false);
        btn.setBorderPainted(true);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);

        btn.setBackground(new Color(27, 30, 38)); // #1b1e26
        btn.setForeground(Color.WHITE);
        btn.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(153, 255, 51)));

        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }
    
    
    
    private void loadDataTable(String keyword) {
       try {
        String query = """
            SELECT
                i.id,
                c.name AS category,
                i.name,
                i.code,
                i.jumlah,
                i.item_condition,
                i.notes,
                i.created_at
            FROM items i
            LEFT JOIN categories c
                ON i.category_id = c.id
            WHERE 
                CAST(i.id AS CHAR) LIKE ? OR
                i.name LIKE ? OR
                i.code LIKE ? OR
                c.name LIKE ? OR
                i.item_condition LIKE ?
            ORDER BY i.id ASC
        """;

        PreparedStatement ps = dbConn.conn.prepareStatement(query);

        String param = "%" + keyword + "%";

        ps.setString(1, param); // ID
        ps.setString(2, param); // name
        ps.setString(3, param); // code
        ps.setString(4, param); // kategori
        ps.setString(5, param); // kondisi

        ResultSet rs = ps.executeQuery();

        DefaultTableModel model = new DefaultTableModel();

        model.addColumn("ID");
        model.addColumn("Kategori");
        model.addColumn("Nama Barang");
        model.addColumn("Kode");
        model.addColumn("Jumlah");
        model.addColumn("Kondisi");
        model.addColumn("Catatan");
        model.addColumn("Tanggal Dibuat");

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("id"),
                rs.getString("category"),
                rs.getString("name"),
                rs.getString("code"),
                rs.getInt("jumlah"),
                rs.getString("item_condition"),
                rs.getString("notes"),
                rs.getTimestamp("created_at")
            });
        }

        dataTable.setModel(model);

        rs.close();
        ps.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal search\n" + e.getMessage());
        }
    }
    
    private void loadCategories() {
        try {
            categoryFilter.removeAllItems();
            categoryFilter.addItem("Semua Kategori");

            String query = "SELECT name FROM categories ORDER BY name";
            PreparedStatement ps = dbConn.conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                categoryFilter.addItem(rs.getString("name"));
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
    
    private void loadDataByCategory(String category) {
        try {
            String query = """
                SELECT
                    i.id,
                    c.name AS category,
                    i.name,
                    i.code,
                    i.jumlah,
                    i.item_condition,
                    i.notes,
                    i.created_at
                FROM items i
                LEFT JOIN categories c
                    ON i.category_id = c.id
                WHERE c.name = ?
                ORDER BY i.id ASC
            """;

            PreparedStatement ps = dbConn.conn.prepareStatement(query);
            ps.setString(1, category);

            ResultSet rs = ps.executeQuery();

            DefaultTableModel model = (DefaultTableModel) dataTable.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("category"),
                    rs.getString("name"),
                    rs.getString("code"),
                    rs.getInt("jumlah"),
                    rs.getString("item_condition"),
                    rs.getString("notes"),
                    rs.getTimestamp("created_at")
                });
            }

            dataTable.setModel(model);

            rs.close();
            ps.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
    
    private void loadDataTable() {
        loadDataTable("");
    }
    
    public void refreshData() {
        loadDataTable();
    }
    
    public void refreshCategoryData() {
        loadCategories();
    }

    /**
     * Creates new form MainMenu
     */
    public MainMenu() {
        initComponents();
        styleTable();
        styleButton(addBtn);
        styleButton(editBtn);
        styleButton(deleteBtn);
        styleButton(addCategory);
        styleButton(deleteCategory);
        styleButton(searchBtn);
        //setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(30, 27, 38));
        dbConn = new connection();
        loadDataTable();
        loadCategories();
        searchField.setCaretColor(Color.WHITE);
        searchField.setSelectionColor(new Color(153, 255, 51));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        dataTable = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        searchField = new javax.swing.JTextField();
        searchBtn = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(100, 0), new java.awt.Dimension(100, 0), new java.awt.Dimension(100, 32767));
        addBtn = new javax.swing.JButton();
        editBtn = new javax.swing.JButton();
        deleteBtn = new javax.swing.JButton();
        addCategory = new javax.swing.JButton();
        deleteCategory = new javax.swing.JButton();
        categoryFilter = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Google Sans", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(153, 255, 51));
        jLabel1.setText("Khaslana Inventaris");

        dataTable.setBackground(new java.awt.Color(30, 27, 38));
        dataTable.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 255, 51), 2, true));
        dataTable.setFont(new java.awt.Font("Google Sans", 0, 14)); // NOI18N
        dataTable.setForeground(new java.awt.Color(255, 255, 255));
        dataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        dataTable.setGridColor(new java.awt.Color(153, 255, 51));
        dataTable.setSelectionForeground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setViewportView(dataTable);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Cari Barang");

        searchField.setBackground(new java.awt.Color(30, 27, 38));
        searchField.setForeground(new java.awt.Color(255, 255, 255));
        searchField.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 255, 51), 1, true));
        searchField.addActionListener(this::searchFieldActionPerformed);

        searchBtn.setBackground(new java.awt.Color(30, 27, 38));
        searchBtn.setForeground(new java.awt.Color(255, 255, 255));
        searchBtn.setText("Cari Barang");
        searchBtn.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 255, 51), 1, true));
        searchBtn.addActionListener(this::searchBtnActionPerformed);

        jLabel3.setBackground(new java.awt.Color(30, 27, 38));
        jLabel3.setFont(new java.awt.Font("Google Sans", 1, 16)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(153, 255, 51));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("AKSI");

        addBtn.setBackground(new java.awt.Color(30, 27, 38));
        addBtn.setForeground(new java.awt.Color(255, 255, 255));
        addBtn.setText("Tambah Barang");
        addBtn.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 255, 51), 1, true));
        addBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addBtnMouseClicked(evt);
            }
        });
        addBtn.addActionListener(this::addBtnActionPerformed);

        editBtn.setBackground(new java.awt.Color(30, 27, 38));
        editBtn.setForeground(new java.awt.Color(255, 255, 255));
        editBtn.setText("Edit Barang");
        editBtn.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 255, 51), 1, true));
        editBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                editBtnMouseClicked(evt);
            }
        });
        editBtn.addActionListener(this::editBtnActionPerformed);

        deleteBtn.setBackground(new java.awt.Color(30, 27, 38));
        deleteBtn.setForeground(new java.awt.Color(255, 255, 255));
        deleteBtn.setText("Hapus Barang");
        deleteBtn.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 255, 51), 1, true));
        deleteBtn.addActionListener(this::deleteBtnActionPerformed);

        addCategory.setBackground(new java.awt.Color(30, 27, 38));
        addCategory.setForeground(new java.awt.Color(255, 255, 255));
        addCategory.setText("Tambah Kategori");
        addCategory.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 255, 51), 1, true));
        addCategory.addActionListener(this::addCategoryActionPerformed);

        deleteCategory.setBackground(new java.awt.Color(30, 27, 38));
        deleteCategory.setForeground(new java.awt.Color(255, 255, 255));
        deleteCategory.setText("Hapus Kategori");
        deleteCategory.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 255, 51), 1, true));
        deleteCategory.addActionListener(this::deleteCategoryActionPerformed);

        categoryFilter.setBackground(new java.awt.Color(30, 27, 38));
        categoryFilter.setForeground(new java.awt.Color(255, 255, 255));
        categoryFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        categoryFilter.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 255, 51), 1, true));
        categoryFilter.addActionListener(this::categoryFilterActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 328, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(159, 159, 159))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, 426, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(searchBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(categoryFilter, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(editBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                    .addComponent(deleteBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                    .addComponent(addCategory, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                    .addComponent(deleteCategory, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(searchBtn)
                        .addComponent(categoryFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(addBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addCategory)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteCategory)
                        .addContainerGap(321, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        // TODO add your handling code here:
        searchBtnActionPerformed(evt);
    }//GEN-LAST:event_searchFieldActionPerformed

    private void searchBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBtnActionPerformed
        // TODO add your handling code here:                                
        String keyword = searchField.getText().trim();
        loadDataTable(keyword);
    }//GEN-LAST:event_searchBtnActionPerformed

    private void addBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBtnActionPerformed
        // TODO add your handling code here:
        AddItemFrame form = new AddItemFrame(this);
        form.setLocationRelativeTo(this);
        form.setVisible(true);
    }//GEN-LAST:event_addBtnActionPerformed

    private void editBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editBtnActionPerformed
        // TODO add your handling code here:
        int selectedRow = dataTable.getSelectedRow();
        
        if (selectedRow != -1) {
            int itemId = (int) dataTable.getValueAt(selectedRow, 0);
                    
            EditItemDialog dialog = new EditItemDialog (this,true, itemId);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Pilih barang yang ingin diedit terlebih dahulu pada tabel!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_editBtnActionPerformed

    private void deleteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteBtnActionPerformed
        // TODO add your handling code here:
        DeleteItemFrame dialog = new DeleteItemFrame(this, this, true);
        
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }//GEN-LAST:event_deleteBtnActionPerformed

    private void addCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCategoryActionPerformed
        new AddCategoryFrame(this).setVisible(true);
    }//GEN-LAST:event_addCategoryActionPerformed

    private void deleteCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteCategoryActionPerformed
        DeleteCategoryDialog dialog = new DeleteCategoryDialog(this, this, true);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }//GEN-LAST:event_deleteCategoryActionPerformed

    private void addBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addBtnMouseClicked
        
    }//GEN-LAST:event_addBtnMouseClicked

    private void editBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editBtnMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_editBtnMouseClicked

    private void categoryFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_categoryFilterActionPerformed
        Object selected = categoryFilter.getSelectedItem();

        if (selected == null) {
            return;
        }
        String selectedCategory = selected.toString();

        if (selectedCategory.equals("Semua Kategori")) {
            loadDataTable();
        } else {
            loadDataByCategory(selectedCategory);
        }
    }//GEN-LAST:event_categoryFilterActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new MainMenu().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addBtn;
    private javax.swing.JButton addCategory;
    private javax.swing.JComboBox<String> categoryFilter;
    private javax.swing.JTable dataTable;
    private javax.swing.JButton deleteBtn;
    private javax.swing.JButton deleteCategory;
    private javax.swing.JButton editBtn;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton searchBtn;
    private javax.swing.JTextField searchField;
    // End of variables declaration//GEN-END:variables
}
