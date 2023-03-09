package icbm.classic.prefab.gui;

import icbm.classic.content.blocks.launcher.cruise.gui.GuiCruiseLauncher;
import icbm.classic.lib.LanguageUtility;
import icbm.classic.lib.colors.ColorHelper;
import icbm.classic.lib.transform.region.Rectangle;
import icbm.classic.prefab.gui.tooltip.IToolTip;
import lombok.Setter;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class TextInput<Output> extends GuiTextField implements IToolTip, IGuiComponent {

    private final static int ERROR_COLOR = ColorHelper.toARGB(255, 1, 1, 255);

    // Selection change handling
    @Setter
    private Consumer<Boolean> focusChangedCallback;

    // Output to text handling
    @Setter
    private Function<Output, String> parseOutput;

    // Error handling
    private String errorFeedback = null;

    // Data watcher to know when we update our display text
    @Setter
    private Supplier<Output> sourceWatcher;
    @Setter
    private Consumer<Output> onSourceChange;
    private Output previousData;

    @Deprecated
    private final Rectangle boundBox;

    public TextInput(int componentId, FontRenderer fontrendererObj, int x, int y, int width, int height) {
        super(componentId, fontrendererObj, x, y, width, height);
        boundBox = new Rectangle(x, y, x + width + 1, y + height + 1); //TODO replace with internal check using data stored
    }

    public static TextInput<Vec3d> vec3dField(int id, FontRenderer fontRenderer, int x, int y, int width, int height,
                                              Supplier<Vec3d> getter, Consumer<Vec3d> setter, Consumer<Vec3d> network
    ) {
        final TextInput<Vec3d> fieldTarget = new TextInput<Vec3d>(id, fontRenderer, x, y, width, height);
        fieldTarget.simpleHandler(getter, setter, GuiFormatHelpers::parseVec3d);
        fieldTarget.setParseOutput(LanguageUtility::posFormatted);
        fieldTarget.setOnSourceChange(network);
        return fieldTarget;
    }

    public static TextInput<Integer> intField(int id, FontRenderer fontRenderer, int x, int y, int width, int height,
                                              Supplier<Integer> getter, Consumer<Integer> setter, Consumer<Integer> network
    ) {
        final TextInput<Integer> fieldTarget = new TextInput<Integer>(id, fontRenderer, x, y, width, height);
        fieldTarget.simpleHandler(getter, setter, GuiFormatHelpers::parseInt);
        fieldTarget.setOnSourceChange(network);
        return fieldTarget;
    }

    public static TextInput<String> textField(int id, FontRenderer fontRenderer, int x, int y, int width, int height,
                                              Supplier<String> getter, Consumer<String> setter, Consumer<String> network
    ) {
        final TextInput<String> fieldTarget = new TextInput<String>(id, fontRenderer, x, y, width, height);
        fieldTarget.stringHandler(getter, setter);
        fieldTarget.setOnSourceChange(network);
        return fieldTarget;
    }

    @Override
    public void onUpdate() {
        if(!isFocused() && sourceWatcher != null) {
            detectForChange();
        }
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected boolean detectForChange() {
        final Output output = sourceWatcher.get();
        if(!Objects.equals(output, previousData)) {
            previousData = output;
            if(parseOutput != null) {
                setText(parseOutput.apply(output));
            }
            else {
                setText(output.toString());
            }
            return true;
        }
        return false;
    }

    @Override
    public void setFocused(boolean isFocusedIn) {
        super.setFocused(isFocusedIn);
        // Reset error feedback when we de-select
        if(!isFocusedIn) {
            errorFeedback = null;
        }

        // Handle focus change
        if (focusChangedCallback != null) {
            focusChangedCallback.accept(isFocusedIn);
        }

        // Handle source change caused by user
        if(!isFocusedIn && detectForChange() && onSourceChange != null) {
            onSourceChange.accept(previousData);
        }
    }

    public TextInput<Output> simpleHandler(Supplier<Output> getter, Consumer<Output> setter, BiFunction<String, Consumer<Output>, String> validator) {
        sourceWatcher = getter;
        focusChangedCallback = (state) -> {
            if(!state) {
                // Parse input from user and store into tile client side
                errorFeedback = validator.apply(getText(), setter);
            }
        };
        return this;
    }

    public TextInput<String> stringHandler(Supplier<String> getter, Consumer<String> setter) {
        sourceWatcher = (Supplier<Output>) getter;
        focusChangedCallback = (state) -> {
            if(!state) {
                setter.accept(getText());
            }
        };
        return (TextInput<String>) this;
    }

    @Override
    public void drawForegroundLayer(int mouseX, int mouseY) {
        drawTextBox();
    }

    public boolean onKeyTyped(char key, int keyId) {
        if(isFocused()) {
            textboxKeyTyped(key, keyId);
            return true;
        }
        return false;
    }

    @Override
    public void drawTextBox() {
        super.drawTextBox();
        if (this.getVisible() && this.getEnableBackgroundDrawing() && isErrored()) {
            drawHorizontalLine(this.x, this.x + this.width, this.y + this.height, ERROR_COLOR);
        }
    }

    @Override
    public boolean isWithin(int cursorX, int cursorY) {
        return boundBox.isWithin(cursorX, cursorY);
    }

    @Override
    public String getTooltip() {
        return getErrorFeedback();
    }

    public String getErrorFeedback() {
        return errorFeedback;
    }

    public void setError(String error) {
        this.errorFeedback = error;
    }

    public boolean isErrored() {
        return errorFeedback != null;
    }
}
