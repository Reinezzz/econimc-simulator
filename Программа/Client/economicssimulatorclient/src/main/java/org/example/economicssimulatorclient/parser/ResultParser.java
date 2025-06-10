package org.example.economicssimulatorclient.parser;

/**
 * Интерфейс для парсера результата экономической модели.
 */
public interface ResultParser {
    /**
     * Преобразует "сырой" JSON-результат в человеко-читаемое представление.
     * @param json Строка-JSON результата модели.
     * @return Отформатированный результат для отображения пользователю.
     */
    String parse(String json);
}
