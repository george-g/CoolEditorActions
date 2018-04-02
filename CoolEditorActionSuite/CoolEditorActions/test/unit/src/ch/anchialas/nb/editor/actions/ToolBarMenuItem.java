/*
 * Copyright 2012 Anchialas <anchialas@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.anchialas.nb.editor.actions;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JToolBar;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.event.MouseInputListener;

class ToolBarMenuItem extends JToolBar implements MenuElement {

   Color savedForeground = null;
   private static MenuElement NO_SUB_ELEMENTS[] = new MenuElement[0];

   public ToolBarMenuItem(String label) {
      super(label);
      init();
   }

   private void init() {
      setAlignmentY(Component.LEFT_ALIGNMENT);
      //setUI(new BasicToolBarUI());
      setBorderPainted(false);
      setFloatable(false);
      setRollover(true);
      
      updateUI();
      setRequestFocusEnabled(false);
      // Borrows heavily from BasicMenuUI
      MouseInputListener mouseInputListener = new MouseInputListener() {
         // If mouse released over this menu item, activate it
         @Override
         public void mouseReleased(MouseEvent mouseEvent) {
            MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
            Point point = mouseEvent.getPoint();
            if ((point.x >= 0) && (point.x < getWidth()) && (point.y >= 0) && (point.y < getHeight())) {
               menuSelectionManager.clearSelectedPath();
               // Component automatically handles "selection" at this point
               // doClick(0); // not necessary
            } else {
               menuSelectionManager.processMouseEvent(mouseEvent);
            }
         }

         // If mouse moves over menu item, add to selection path, so it becomes armed
         @Override
         public void mouseEntered(MouseEvent mouseEvent) {
            MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
            menuSelectionManager.setSelectedPath(getPath());
         }

         // When mouse moves away from menu item, disarm it and select something else
         @Override
         public void mouseExited(MouseEvent mouseEvent) {
            MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
            MenuElement path[] = menuSelectionManager.getSelectedPath();
            if (path.length > 1) {
               MenuElement newPath[] = new MenuElement[path.length - 1];
               for (int i = 0, c = path.length - 1; i < c; i++) {
                  newPath[i] = path[i];
               }
               menuSelectionManager.setSelectedPath(newPath);
            }
         }

         // Pass along drag events
         @Override
         public void mouseDragged(MouseEvent mouseEvent) {
            MenuSelectionManager.defaultManager().processMouseEvent(mouseEvent);
         }

         @Override
         public void mouseClicked(MouseEvent mouseEvent) {
         }

         @Override
         public void mousePressed(MouseEvent mouseEvent) {
         }

         public void mouseMoved(MouseEvent mouseEvent) {
         }
      };
      addMouseListener(mouseInputListener);
      addMouseMotionListener(mouseInputListener);
   }

   // MenuElement methods
   @Override
   public Component getComponent() {
      return this;
   }

   @Override
   public MenuElement[] getSubElements() {
      // No subelements
      return NO_SUB_ELEMENTS;
   }

   @Override
   public void menuSelectionChanged(boolean isIncluded) {

      if (isIncluded) {
         savedForeground = getForeground();
         if (!savedForeground.equals(Color.BLUE)) {
            setForeground(Color.BLUE);
         } else {
            // In case foreground blue, use something different
            setForeground(Color.RED);
         }
      } else {
         setForeground(savedForeground);
         // If null, get foreground from installed look and feel
         if (savedForeground == null) {
            updateUI();
         }
      }
   }

   @Override
   public void processKeyEvent(KeyEvent keyEvent, MenuElement path[], MenuSelectionManager manager) {
      super.processKeyEvent(keyEvent);
   }

   @Override
   public void processMouseEvent(MouseEvent mouseEvent, MenuElement path[],
                                 MenuSelectionManager manager) {
      super.processMouseEvent(mouseEvent);
   }

   // Borrows heavily from BasicMenuItemUI.getPath()
   private MenuElement[] getPath() {
      MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
      MenuElement oldPath[] = menuSelectionManager.getSelectedPath();
      MenuElement newPath[];
      int oldPathLength = oldPath.length;
      if (oldPathLength == 0) {
         return new MenuElement[0];
      }
      Component parent = getParent();
      if (oldPath[oldPathLength - 1].getComponent() == parent) {
         // Going deeper under the parent menu
         newPath = new MenuElement[oldPathLength + 1];
         System.arraycopy(oldPath, 0, newPath, 0, oldPathLength);
         newPath[oldPathLength] = this;
      } else {
         // Sibling/child menu item currently selected
         int newPathPosition;
         for (newPathPosition = oldPath.length - 1; newPathPosition >= 0; newPathPosition--) {
            if (oldPath[newPathPosition].getComponent() == parent) {
               break;
            }
         }
         newPath = new MenuElement[newPathPosition + 2];
         System.arraycopy(oldPath, 0, newPath, 0, newPathPosition + 1);
         newPath[newPathPosition + 1] = this;
      }
      return newPath;
   }
}