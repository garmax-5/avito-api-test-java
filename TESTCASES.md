POST /api/1/item — Сохранить объявление

## testcase1-1. Сохранение объявления с валидными данными

**Предусловие:**

**Headers request:**

- Content-Type: application/json
- Accept: application/json

**Тестовые данные:**

**Тело запроса:**

```json
{
  "sellerID": 111111,
  "name": "testItem",
  "price": 9900,
  "statistics": {
  "likes": 21,
  "viewCount": 11,
  "contacts": 43
  }
}
```

**Шаги:**

1. Отправить POST-запрос на https://qa-internship.avito.com/api/1/item
2. Проверить код состояния
3. Проверить тело ответа от сервера

**Ожидаемый результат:**

1. Запрос успешно отправлен на сервер
2. HTTP Status: 200 OK
3. Создан первый объект объявления с id = <генерируется новое значение> Тело ответа в формате JSON возвращается от сервера и будет иметь следующий вид:

```json
{
  "id": <генерация нового значения типа string>,
  "sellerId": 111111,
  "name": "testItem",
  "price": 9900,
  "createdAt": <генерация нового значения типа string>,
  "statistics": {
  "likes": 21,
  "viewCount": 11,
  "contacts": 43
  }
}
```

---

## testcase1-2. Сохранение объявления с отсутствием обязательных полей

**Предусловие:**

**Headers request:**

- Content-Type: application/json
- Accept: application/json

**Тестовые данные:**

```text
отсутствует sellerID: { "name": "testItem", "price": 9900, "statistics": {"likes":21,"viewCount":11,"contacts":43} }
отсутствует name: { "sellerID":111111, "price":9900, "statistics":{"likes":21,"viewCount":11,"contacts":43} }
отсутствует price: { "sellerID":111111, "name":"testItem", "statistics":{"likes":21,"viewCount":11,"contacts":43} }
отсутствует statistics: { "sellerID":111111, "name":"testItem", "price":9900 }
```

**Шаги:**

1. Отправить POST-запрос на https://qa-internship.avito.com/api/1/item
2. Проверить код состояния
3. Проверить тело ответа от сервера

**Ожидаемый результат:**

1. Запрос не выполнен из-за отсутствия обязательного поля
2. HTTP Status: 400 Bad Request
3. Тело ответа в формате JSON возвращается от сервера и содержит информацию:

```json
{
  "result": {
    "message": "<string>",
    "messages": {
      "<string>": "<string>"
    }
  },
  "status": "<string>"
}
```

---

## testcase1-3. Сохранение объявления с некорректным типом идентификатора

**Предусловие:**

**Headers request:**

- Content-Type: application/json
- Accept: application/json

**Тестовые данные:**

**Тело запроса:**

```json
{
  "sellerID": "test111111",
  "name": "testItem",
  "price": 9900,
  "statistics": {
  "likes": 21,
  "viewCount": 11,
  "contacts": 43
  }
}
```

**Шаги:**

1. Отправить POST-запрос на https://qa-internship.avito.com/api/1/item
2. Проверить код состояния
3. Проверить тело ответа от сервера

**Ожидаемый результат:**

1. Запрос не выполнен из-за некорректного типа поля sellerID
2. HTTP Status: 400 Bad Request
3. Тело ответа в формате JSON возвращается от сервера и содержит информацию:

```json
{
  "result": {
    "message": "<string>",
    "messages": {
      "<string>": "<string>"
    }
  },
  "status": "<string>"
}
```

---

## testcase1-4. Сохранение объявления с некорректными типами полей внутри statistics

**Предусловие:**

**Headers request:**

- Content-Type: application/json
- Accept: application/json

**Тестовые данные:**

```text
некорректно поле likes: { "name": "sellerID": 111111, "testItem", "price": 9900, "statistics": {"likes":"twenty one","viewCount":11,"contacts":43} }
некорректно поле viewCount: { "name": "sellerID": 111111, "testItem", "price": 9900, "statistics": {"likes":21,"viewCount":"eleven","contacts":43} }
некорректно поле contacts: { "name": "sellerID": 111111, "testItem", "price": 9900, "statistics": {"likes":21,"viewCount":11,"contacts":"forty three"} }
```

**Шаги:**

1. Отправить POST-запрос на https://qa-internship.avito.com/api/1/item
2. Проверить код состояния
3. Проверить тело ответа от сервера

**Ожидаемый результат:**

1. Запрос не выполнен из-за некорректных типов полей
2. HTTP Status: 400 Bad Request
3. Тело ответа в формате JSON возвращается от сервера и содержит информацию:

```json
{
  "result": {
    "message": "<string>",
    "messages": {
      "<string>": "<string>"
    }
  },
  "status": "<string>"
}
```

---

## testcase1-5. Сохранение объявления с некорректными типом поля price

**Предусловие:**

**Headers request:**

- Content-Type: application/json
- Accept: application/json

**Тестовые данные:**

**Тело запроса:**

```json
{
  "sellerID": 111111,
  "name": "testItem",
  "price": "9900",
  "statistics": {
  "likes": 21,
  "viewCount": 11,
  "contacts": 43
  }
}
```

**Шаги:**

1. Отправить POST-запрос на https://qa-internship.avito.com/api/1/item
2. Проверить код состояния
3. Проверить тело ответа от сервера

**Ожидаемый результат:**

1. Запрос не выполнен из-за некорректного типа поля
2. HTTP Status: 400 Bad Request
3. Тело ответа в формате JSON возвращается от сервера и содержит информацию:

```json
{
  "result": {
    "message": "<string>",
    "messages": {
      "<string>": "<string>"
    }
  },
  "status": "<string>"
}
```

---

## testcase1-6. Отправка невалидного JSON

**Предусловие:**

**Headers request:**

- Content-Type: application/json
- Accept: application/json

**Тестовые данные:**

**Тело запроса:**

```json
{
  "sellerID": 111111,
  "name": "testItem",
  "price": 9900,
  "statistics": {
  "likes": 21,
  "viewCount": 11,
  "contacts": 43
}
```

**Шаги:**

1. Отправить POST-запрос на https://qa-internship.avito.com/api/1/item
2. Проверить код состояния
3. Проверить тело ответа от сервера

**Ожидаемый результат:**

1. Запрос не выполнен из-за невалидного JSON
2. HTTP Status: 400 Bad Request
3. Тело ответа в формате JSON возвращается от сервера и содержит информацию:

```json
{
  "result": {
    "message": "<string>",
    "messages": {
      "<string>": "<string>"
    }
  },
  "status": "<string>"
}
```

---

GET /api/1/item/{id} — Получить объявление по идентификатору

## testcase2-1. Получение объявления с существующим идентификатором

**Предусловие:**

1. Headers request:

- Content-Type: application/json
- Accept: application/json

2. Сохранить объявление через POST-запрос, сохранить id из ответа

**Шаги:**

1. Отправить GET-запрос на https://qa-internship.avito.com/api/1/item/{id}
2. Проверить код состояния
3. Проверить тело ответа от сервера

**Ожидаемый результат:**

1. Запрос успешно отправлен на сервер
2. HTTP Status: 200 OK
3. Тело ответа в формате JSON возвращается от сервера и содержит информацию:

```json
[
  {
  "id": "",
  "sellerId": ,
  "name": "",
  "price": ,
  "createdAt": "",
  "statistics": {
  "likes": ,
  "viewCount": ,
  "contacts":
  }
  }
]
```

---

## testcase2-2. Получение объявления с несуществующим идентификатором

**Предусловие:**

**Headers request:**

- Content-Type: application/json
- Accept: application/json

**Шаги:**

1. Отправить GET-запрос на https://qa-internship.avito.com/api/1/item/{id}
2. Проверить код состояния
3. Проверить тело ответа от сервера

**Ожидаемый результат:**

1. Запрос успешно отправлен на сервер
2. HTTP Status: 404 Not Found
3. Тело ответа в формате JSON возвращается от сервера и содержит информацию:

```json
{
  "result": "",
  "status": ""
}
```

---

## testcase2-3. Получение объявления с невалидным идентификатором

**Предусловие:**

**Headers request:**

- Content-Type: application/json
- Accept: application/json

**Шаги:**

1. Отправить GET-запрос на https://qa-internship.avito.com/api/1/item/{id}
2. Проверить код состояния
3. Проверить тело ответа от сервера

**Ожидаемый результат:**

1. Запрос отклонён из-за некорректного значения идентификатора
2. HTTP Status: 400 Bad Request
3. Тело ответа в формате JSON возвращается от сервера и содержит информацию:

```json
{
  "result": {
    "message": "<string>",
    "messages": {
      "<string>": "<string>"
    }
  },
  "status": "<string>"
}
```

---

GET /api/1/{sellerID}/item — Получить все объявления продавца

## testcase3-1. Получение всех объявлений существующего продавца

**Предусловие:**

**Headers request:**

- Content-Type: application/json
- Accept: application/json

**Шаги:**

1. Отправить GET-запрос на https://qa-internship.avito.com/api/1/{sellerID}/item
2. Проверить код состояния
3. Проверить тело ответа от сервера

**Ожидаемый результат:**

1. Запрос успешно отправлен на сервер
2. HTTP Status: 200 OK
3. Тело ответа в формате JSON возвращается от сервера и содержит информацию:

```json
[
  {
  "id": "",
  "sellerId": ,
  "name": "",
  "price": ,
  "createdAt": "",
  "statistics": {
  "likes": ,
  "viewCount": ,
  "contacts":
  }
  }
]
```

---

## testcase3-2. Получение всех объявлений несуществующего продавца

**Предусловие:**

**Headers request:**

- Content-Type: application/json
- Accept: application/json

**Шаги:**

1. Отправить GET-запрос на https://qa-internship.avito.com/api/1/{sellerID}/item
2. Проверить код состояния
3. Проверить тело ответа от сервера

**Ожидаемый результат:**

1. Запрос успешно отправлен на сервер
2. HTTP Status: 200 OK
3. Тело ответа в формате JSON возвращается от сервера и содержит информацию:

```json
[]
```

---

## testcase3-3. Получение всех объявлений c невалидным идентификатором

**Предусловие:**

**Headers request:**

- Content-Type: application/json
- Accept: application/json

**Шаги:**

1. Отправить GET-запрос на https://qa-internship.avito.com/api/1/{sellerID}/item
2. Проверить код состояния
3. Проверить тело ответа от сервера

**Ожидаемый результат:**

1. Запрос отклонён из-за некорректного значения sellerID
2. HTTP Status: 400 Bad Request
3. Тело ответа в формате JSON возвращается от сервера и содержит информацию:

```json
{
  "result": {
    "message": "<string>",
    "messages": {
      "<string>": "<string>"
    }
  },
  "status": "<string>"
}
```

---

GET /api/1/statistic/{id} — Получить статистику по объявлению

## testcase4-1. Получение статистики по существующему идентификатору

**Предусловие:**

1. Headers request:

- Content-Type: application/json
- Accept: application/json

2. Сохранить объявление через POST-запрос, сохранить id из ответа

**Шаги:**

1. Отправить GET-запрос на https://qa-internship.avito.com/api/1/statistic/{id}
2. Проверить код состояния
3. Проверить тело ответа от сервера

**Ожидаемый результат:**

1. Запрос успешно отправлен на сервер
2. HTTP Status: 200 OK
3. Тело ответа в формате JSON возвращается от сервера и содержит информацию:

```json
[
  {
  "likes": ,
  "viewCount": ,
  "contacts":
  }
]
```

---

## testcase4-2. Получение статистики по несуществующему идентификатору

**Предусловие:**

**Headers request:**

- Content-Type: application/json
- Accept: application/json

**Шаги:**

1. Отправить GET-запрос на https://qa-internship.avito.com/api/1/statistic/{id}
2. Проверить код состояния
3. Проверить тело ответа от сервера

**Ожидаемый результат:**

1. Запрос успешно отправлен на сервер
2. HTTP Status: 404 Not Found
3. Тело ответа в формате JSON возвращается от сервера и содержит информацию:

```json
{
  "result": "",
  "status": ""
}
```

---

## testcase4-3. Получение статистики с невалидным идентификатором

**Предусловие:**

**Headers request:**

- Content-Type: application/json
- Accept: application/json

**Шаги:**

1. Отправить GET-запрос на https://qa-internship.avito.com/api/1/statistic/{id}
2. Проверить код состояния
3. Проверить тело ответа от сервера

**Ожидаемый результат:**

1. Запрос отклонён из-за некорректного значения id
2. HTTP Status: 400 Bad Request
3. Тело ответа в формате JSON возвращается от сервера и содержит информацию:

```json
{
  "result": {
    "message": "<string>",
    "messages": {
      "<string>": "<string>"
    }
  },
  "status": "<string>"
}
```

---

GET /api/2/statistic/{id} — Получить статистику по объявлению

## testcase5-1. Получение статистики по существующему идентификатору

**Предусловие:**

1. Headers request:

- Content-Type: application/json
- Accept: application/json

2. Сохранить объявление через POST-запрос, сохранить id из ответа

**Шаги:**

1. Отправить GET-запрос на https://qa-internship.avito.com/api/2/statistic/{id}
2. Проверить код состояния
3. Проверить тело ответа от сервера

**Ожидаемый результат:**

1. Запрос успешно отправлен на сервер
2. HTTP Status: 200 OK
3. Тело ответа в формате JSON возвращается от сервера и содержит информацию:

```json
[
  {
  "likes": ,
  "viewCount": ,
  "contacts":
  }
]
```

---

## testcase5-2. Получение статистики по несуществующему идентификатору

**Предусловие:**

**Headers request:**

- Content-Type: application/json
- Accept: application/json

**Шаги:**

1. Отправить GET-запрос на https://qa-internship.avito.com/api/2/statistic/{id}
2. Проверить код состояния
3. Проверить тело ответа от сервера

**Ожидаемый результат:**

1. Запрос успешно отправлен на сервер
2. HTTP Status: 404 Not Found
3. Тело ответа в формате JSON возвращается от сервера и содержит информацию:

```json
{
  "result": "",
  "status": ""
}
```

---

## testcase5-3. Получение статистики с невалидным идентификатором

**Предусловие:**

**Headers request:**

- Content-Type: application/json
- Accept: application/json

**Шаги:**

1. Отправить GET-запрос на https://qa-internship.avito.com/api/2/statistic/{id}
2. Проверить код состояния
3. Проверить тело ответа от сервера

**Ожидаемый результат:**

1. Запрос успешно отправлен на сервер
2. HTTP Status: 404 Not Found
3. Тело ответа в формате JSON возвращается от сервера и содержит информацию:

```json
{
  "result": "",
  "status": ""
}
```
