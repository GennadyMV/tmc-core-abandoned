package fi.helsinki.cs.tmc.core;

import fi.helsinki.cs.tmc.core.ui.UIInvoker;

public final class Core {
    
    private static UIInvoker uiInvoker;
    
    public static void initialize(final UIInvoker uiInvoker) {
        
        Core.uiInvoker = uiInvoker;
    }
    
    public static UIInvoker getUIInvoker() {
        
        return Core.uiInvoker;
    }
}
