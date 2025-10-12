package lc.cit.list;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class TextureListScreen extends Screen {
    private final Screen parent;
    private final List<String> mappings;
    private final List<String> itemlist;
    private final List<String> conditionList;
    private MappingsListWidget list;

    public TextureListScreen(Screen parent) {
        super(Text.literal("Renameable CIT Textures"));
        this.parent = parent;
        BundleWrapper bundle = CitScanner.getAllCustomNameCITs();
        this.mappings = bundle.formatedStringLines;
        this.itemlist = bundle.itemNames;
        this.conditionList =bundle.toRenameTrigger;
    }

    @Override
    protected void init() {
        super.init();

        int fontHeight = this.textRenderer.fontHeight;
        int itemHeight = fontHeight + 4; // small padding
        int top = 20;
        int bottom = top + itemHeight;

        this.list = new MappingsListWidget(this.client, this.width - 10, this.height - 50, top, bottom, itemHeight);

        
        for (int i = 0; i < mappings.size(); i++) {
            Identifier id = Identifier.of("minecraft", itemlist.get(i));
            Item item = Registries.ITEM.get(id);
            ItemStack stack = new ItemStack(item);
            stack.set(DataComponentTypes.CUSTOM_NAME, Text.literal(conditionList.get(i))); 
            this.list.addMapping(Text.literal(mappings.get(i)),stack);
            
        }

        this.addSelectableChild(list);
        this.setInitialFocus(list);

        ButtonWidget exitButton = ButtonWidget.builder(
                Text.literal("Exit"),
                button -> {
                    System.out.println("Exit button clicked");
                    MinecraftClient.getInstance().setScreen(parent);
                })
                .dimensions(this.width / 2 - 50, this.height - 25, 100, 20)
                .narrationSupplier(supplier -> Text.literal("Exit button"))
                .build();

        this.addDrawableChild(exitButton);

    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.list.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true; // ✅ buttons and other widgets get priority
        }
        return this.list.mouseClicked(mouseX, mouseY, button); // ✅ fallback to list
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {

        // Title
        context.drawCenteredTextWithShadow(
                this.textRenderer, this.title, this.width / 2, 3, 0xFFFFFFFF);

        int headerY = 20;
        int padding = 8;
        int column1X = padding;
        int column2X = this.width / 3;
        int column3X = 2 * this.width / 3;

        // List Header
        context.fill(0, headerY - 2, this.width, headerY + this.textRenderer.fontHeight + 2, 0xFF333333); // dark
                                                                                                          // background
        context.drawText(this.textRenderer, "Item to Rename", column1X, headerY, 0xFFFFFFFF, true);
        context.drawText(this.textRenderer, "New Name", column2X, headerY, 0xFFFFFFFF, true);
        context.drawText(this.textRenderer, "Resourcepack", column3X, headerY, 0xFFFFFFFF, true);

        this.list.render(context, mouseX, mouseY, delta);

        context.fill(0, this.height - 30, this.width, this.height - 30 + 3, 0xFFAAAAAA); // light gray line
        // Everything Else
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(parent);
    }

    @Override
    public boolean shouldPause() {
        return true;
    }

    // ---------------------------------------------------------------
    // Scrollable list widget
    // ---------------------------------------------------------------
    private static class MappingsListWidget extends AlwaysSelectedEntryListWidget<MappingsListWidget.TextEntry> {
        public MappingsListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
            super(client, width, height, top, bottom, itemHeight);

        }

        public void addMapping(Text text,ItemStack stack) {
            this.addEntry(new TextEntry(text,stack));
        }

        @Override
        public int getRowWidth() {
            return this.width - 12; // leave space for scrollbar
        }

        public static class TextEntry extends AlwaysSelectedEntryListWidget.Entry<TextEntry> {
            private final Text text;
            private final ItemStack stack;

            public TextEntry(Text text, ItemStack stack) {
                this.text = text;
                this.stack = stack;
            }

            @Override
            public void render(
                    DrawContext context,
                    int index, int y, int x,
                    int entryWidth, int entryHeight,
                    int mouseX, int mouseY,
                    boolean hovered, float tickDelta) {

                MinecraftClient mc = MinecraftClient.getInstance();
                int color = hovered ? 0xFFFFFFA0 : 0xFFFFFFFF;
                int textY = y + (entryHeight - mc.textRenderer.fontHeight) / 2;


int iconX = x + 4;
int iconY = y + (entryHeight - 16) / 2;


context.drawItem(stack, iconX, iconY);


                // ✅ Ensure text width fits in visible area
                String visible = mc.textRenderer.trimToWidth(text.getString(), entryWidth - 10);
                int textX = iconX + 20; // leave room for icon + padding
                context.drawText(mc.textRenderer, visible, textX, textY, color, false);
            }

            @Override
            public Text getNarration() {
                return text;
            }

        }
    }
}
