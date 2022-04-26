package com.rein.android.appfortests;

import static ru.evotor.devices.commons.Constants.DEFAULT_DEVICE_INDEX;

import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import ru.evotor.devices.commons.DeviceServiceConnector;
import ru.evotor.devices.commons.printer.printable.IPrintable;
import ru.evotor.devices.commons.printer.printable.PrintableText;
import ru.evotor.framework.core.IntegrationService;
import ru.evotor.framework.core.action.event.receipt.changes.receipt.print_extra.SetPrintExtra;
import ru.evotor.framework.core.action.event.receipt.print_extra.PrintExtraRequiredEvent;
import ru.evotor.framework.core.action.event.receipt.print_extra.PrintExtraRequiredEventProcessor;
import ru.evotor.framework.core.action.event.receipt.print_extra.PrintExtraRequiredEventResult;
import ru.evotor.framework.core.action.processor.ActionProcessor;
import ru.evotor.framework.receipt.Measure;
import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.Receipt;
import ru.evotor.framework.receipt.ReceiptApi;
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePrintGroupHeader;
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePrintGroupSummary;
import ru.evotor.framework.receipt.print_extras.PrintExtraPlacePrintGroupTop;

public class PrintExtraOld extends IntegrationService {
    /**
     * Получение картинки из каталога asset приложения
     *
     * @param fileName имя файла
     * @return значение типа Bitmap
     */

    private static final String TAG = "MyApp123";
    public static final Locale LOCALE_DEC = Locale.US;
    private DecimalFormat mDecimalFormat = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(LOCALE_DEC));

    @Nullable
    @Override
    protected Map<String, ActionProcessor> createProcessors() {
        Map<String, ActionProcessor> map = new HashMap<>();
        map.put(
                PrintExtraRequiredEvent.NAME_SELL_RECEIPT,
                new PrintExtraRequiredEventProcessor() {
                    @Override
                    public void call(@NotNull String s, @NotNull PrintExtraRequiredEvent printExtraRequiredEvent, @NotNull Callback callback) {
                        List<SetPrintExtra> setPrintExtras = new ArrayList<>();
                        Receipt MyReceipt124 = ReceiptApi.getReceipt(PrintExtraOld.this, Receipt.Type.SELL);
                        Log.d(TAG, "Uuid чека: "+MyReceipt124.getHeader().getNumber());

                        setPrintExtras.add(new SetPrintExtra(
                                //Метод, который указывает место, где будут распечатаны данные.
                                //Данные печатаются после клише и до текста “Кассовый чек”
                                new PrintExtraPlacePrintGroupTop(null),
                                //Массив данных, которые требуется распечатать.
                                new IPrintable[]{
                                        //Простой текст
                                        new PrintableText("Proin eget tortor risus. Nulla quis lorem ut libero malesuada feugiat. Proin eget tortor risus."),
                                        //Штрихкод с контрольной суммой если она требуется для выбранного типа штрихкода
                                        // new PrintableBarcode("4750232005910", PrintableBarcode.BarcodeType.EAN13),
                                        //Изображение
                                        // new PrintableImage(getBitmapFromAsset("ic_launcher.png"))
                                }
                        ));
                        setPrintExtras.add(new SetPrintExtra(
                                //Данные печатаются после текста “Кассовый чек”, до имени пользователя
                                new PrintExtraPlacePrintGroupHeader(null),
                                new IPrintable[]{
                                        //new PrintableBarcode("4750232005910", PrintableBarcode.BarcodeType.EAN13),
                                        //new PrintableText("Proin eget tortor risus. Nulla quis lorem ut libero malesuada feugiat. Proin eget tortor risus.")
                                }
                        ));
                        //Добавляем к каждой позиции чека продажи необходимые данные
                        Receipt r = ReceiptApi.getReceipt(PrintExtraOld.this, Receipt.Type.SELL);
                        if (r != null) {
                            BigDecimal discount = r.getDiscount();
                            if (!BigDecimal.ZERO.equals(discount)) {
                                int width = 0;
                                try {
                                    width = DeviceServiceConnector.getPrinterService().getAllowableSymbolsLineLength(DEFAULT_DEVICE_INDEX);
                                } catch (Throwable e) {

                                }

                                StringBuilder resultDiscount = getString(discount, width, "СКИДКА НА ЧЕК");
                                setPrintExtras.add(new SetPrintExtra(
                                        new PrintExtraPlacePrintGroupSummary(null),
                                        new IPrintable[]{
                                                //  new PrintableText(resultSum.toString()),
                                                new PrintableText(resultDiscount.toString())
                                        }
                                ));
                            }
                        }

                        try {
                            callback.onResult(new PrintExtraRequiredEventResult(setPrintExtras).toBundle());
                        } catch (RemoteException exc) {
                            exc.printStackTrace();
                        }
                    }
                }

        );
        return map;
    }
    @NonNull
    private StringBuilder getString(BigDecimal discount, int width, String text) {
        StringBuilder resultDiscount = new StringBuilder("=")
                .append(mDecimalFormat.format(discount)
                        .replaceAll(",", "."));
        if (width > 0) {
            int spacesLength = width - resultDiscount.length() - text.length();
            for (int i = 0; i < spacesLength; i++) {
                resultDiscount.insert(0, " ");
            }
            resultDiscount.insert(0, text);
        } else {
            resultDiscount.insert(0, text + " ");
        }
        return resultDiscount;
    }

    @NonNull
    private StringBuilder getSumString(double price, int width, String text) {




        List<Position> list = new ArrayList<>();

        String principalInn = "070704218872";
        List<String> phones = new ArrayList<>();
        phones.add("89631654555");

        // for (int i = 1; i < 2; i++) {
        //позиция 1
        list.add(
                Position.Builder.newInstance(
                        //UUID позиции
                        UUID.randomUUID().toString(),
                        //UUID товара
                        UUID.randomUUID().toString(),
                        //Наименование
                        "Товар в кредит",
                        //Наименование единицы измерения
                        new Measure("шт", 0, 0),
                        //Цена без скидок
                        new BigDecimal(1000),
                        //Количество
                        new BigDecimal(1)
                )
                        //.setSettlementMethod(new SettlementMethod.Lend())
                        //.setAgentRequisites(AgentRequisites.createForAgent(principalInn, phones))
                        //.setMark("1234983784289efuiafa930a940939jfe")
                        .build()

        );



        StringBuilder resultDiscount = new StringBuilder("=")
                .append(mDecimalFormat.format(price).replaceAll(",", "."));
        if (width > 0) {
            int spacesLength = width - resultDiscount.length() - text.length();
            for (int i = 0; i < spacesLength; i++) {
                resultDiscount.insert(0, " ");
            }
            resultDiscount.insert(0, text);
        } else {
            resultDiscount.insert(0, text + " ");
        }
        return resultDiscount;
    }
}