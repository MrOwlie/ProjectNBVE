/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author mrowlie
 */
public class LoginScreen implements ScreenController{

    Nifty nifty;
    Screen screen;
    
    public void authenticate() {
        Element userBox = nifty.getCurrentScreen().findElementById("username");
        Element passBox = nifty.getCurrentScreen().findElementById("password");
        String username = userBox.getNiftyControl(TextField.class).getRealText();
        String password = passBox.getNiftyControl(TextField.class).getRealText();
        
        NetWrite.authenticate(username, password);
    }
    
    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
    }

    @Override
    public void onStartScreen() {
        System.out.println("Startscreen");
    }

    @Override
    public void onEndScreen() {
        System.out.println("Endscreen");
    }
    
}
