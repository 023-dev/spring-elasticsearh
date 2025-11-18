# Elasticsearch 학습 가이드

## 개요

이 문서는 Elasticsearch의 기본 개념부터 실제 구현까지를 다룹니다. Elasticsearch는 오픈 소스 분산 RESTful 검색 및 분석 엔진으로, 대규모 데이터 저장 및 빠른 검색 기능을 제공합니다.

### 학습 목표

- Docker를 사용하여 Elasticsearch 환경을 구축할 수 있습니다
- Elasticsearch의 기본 개념(인덱스, 도큐먼트, 매핑)을 이해할 수 있습니다
- REST API를 통해 데이터를 생성, 조회, 수정, 삭제할 수 있습니다
- 역인덱스와 애널라이저의 동작 원리를 이해할 수 있습니다
- Spring Boot와 Elasticsearch를 연동할 수 있습니다

### 사전 준비사항

- Docker 및 Docker Compose 설치
- Postman 또는 cURL 설치
- Java 17 이상 및 Maven 또는 Gradle 설치

---

## Elasticsearch란?

Elasticsearch는 크게 2가지 용도로 사용됩니다:

### 1. 데이터 수집 및 분석
- 로그와 같은 대규모 데이터를 수집 및 분석하는 데 최적화되어 있습니다
- ELK Stack을 활용한 통합 솔루션:
  - **Elasticsearch**: 데이터 저장
  - **Logstash**: 데이터 수집 및 가공
  - **Kibana**: 데이터 시각화

### 2. 검색 최적화
- 대규모 데이터에서도 뛰어난 검색 속도를 제공합니다
- 오타나 동의어를 고려한 유연한 검색 기능을 지원합니다

---

## 설치 및 환경 구성

### 1단계: Docker 환경 준비

디렉터리를 생성합니다:

```bash
mkdir docker-elasticsearch
cd docker-elasticsearch
```

### 2단계: Elasticsearch 설정

`compose.yml` 파일을 생성하고 다음 내용을 작성합니다:

```yaml
services:
  elastic:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.17.4
    ports:
      - 9200:9200
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=false
      - xpack.security.http.ssl.enabled=false
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
```

### 3단계: Elasticsearch 실행

다음 명령어로 Elasticsearch를 실행합니다:

```bash
docker compose up -d
```

### 4단계: 설치 확인

Postman 또는 cURL을 사용하여 다음 요청을 보냅니다:

```bash
GET http://localhost:9200
```

정상적으로 설치되었다면 다음과 유사한 응답을 받습니다:

```json
{
  "name": "9d35788d88ef",
  "cluster_name": "docker-cluster",
  "cluster_uuid": "T8TQn-cGTWyvghdpWuQ3rg",
  "version": {
    "number": "8.17.4",
    "build_flavor": "default",
    "build_type": "docker",
    "build_hash": "c63c7f5f8ce7d2e4805b7b3d842e7e792d84dda1",
    "build_date": "2025-03-20T15:39:59.811110136Z",
    "build_snapshot": false,
    "lucene_version": "9.12.0",
    "minimum_wire_compatibility_version": "7.17.0",
    "minimum_index_compatibility_version": "7.0.0"
  },
  "tagline": "You Know, for Search"
}
```

---

## Kibana 설치

Kibana는 Elasticsearch를 위한 GUI 도구입니다.

### 1단계: Kibana 추가

`compose.yml` 파일을 다음과 같이 수정합니다:

```yaml
services:
  elastic:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.17.4
    ports:
      - 9200:9200
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=false
      - xpack.security.http.ssl.enabled=false
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
      
  kibana:
    image: docker.elastic.co/kibana/kibana:8.17.4
    ports:
      - 5601:5601
    environment:
      - ELASTICSEARCH_HOSTS=["http://elastic:9200"]
```

### 2단계: 컨테이너 재시작

```bash
docker compose down
docker compose up -d
```

### 3단계: Kibana 접속

브라우저에서 `http://localhost:5601`에 접속합니다.

---

## 핵심 개념

### MySQL vs Elasticsearch 용어 비교

| 개념 | MySQL | Elasticsearch |
|------|-------|---------------|
| 데이터 저장소 | 테이블 (Table) | 인덱스 (Index) |
| 데이터 구조 정의 | 스키마 (Schema) | 매핑 (Mapping) |
| 데이터 항목 | 컬럼 (Column) | 필드 (Field) |
| 데이터 단위 | 레코드/로우 (Row) | 도큐먼트 (Document) |

### 작동 방식 비교

Elasticsearch는 REST API를 통해 통신합니다.

**데이터 삽입 비교**

MySQL:
```sql
INSERT INTO users (name, email) VALUES ('John', 'test1234@example.com');
```

Elasticsearch:
```bash
POST http://localhost:9200/users/_doc
Content-Type: application/json

{
  "name": "John",
  "email": "test1234@example.com"
}
```

**데이터 조회 비교**

MySQL:
```sql
SELECT * FROM users;
```

Elasticsearch:
```bash
GET http://localhost:9200/users/_search
Content-Type: application/json

{
  "query": {
    "match_all": {}
  }
}
```

---

## 인덱스 관리

### 인덱스 생성

```bash
PUT /users
```

### 인덱스 조회

```bash
GET /users
```

인덱스가 없으면 `404 - Not Found`를 반환합니다.

### 매핑 정의

매핑은 인덱스에 저장될 데이터의 구조를 정의합니다:

```bash
PUT /users/_mapping
Content-Type: application/json

{
  "properties": {
    "name": {"type": "keyword"},
    "age": {"type": "integer"},
    "is_active": {"type": "boolean"}
  }
}
```

성공하면 `200 - OK`와 함께 `"acknowledged": true`가 반환됩니다.

### 인덱스 삭제

```bash
DELETE /users
```

---

## 도큐먼트 관리

### 도큐먼트 삽입 (ID 자동 생성)

```bash
POST /users/_doc
Content-Type: application/json

{
  "name": "Alice",
  "age": 20,
  "is_active": true
}
```

### 도큐먼트 삽입 (명시적 ID 지정)

```bash
POST /users/_create/1
Content-Type: application/json

{
  "name": "Hee",
  "age": 30,
  "is_active": true
}
```

**주의**: 이미 존재하는 ID를 사용하면 `409 - Conflict` 에러가 발생합니다.

```json
{
  "error": {
    "type": "version_conflict_engine_exception",
    "reason": "[1]: version conflict, document already exists (current version [1])"
  },
  "status": 409
}
```

### 도큐먼트 조회 (전체)

```bash
GET /users/_search
```

응답 예시:

```json
{
  "took": 209,
  "timed_out": false,
  "hits": {
    "total": {
      "value": 2,
      "relation": "eq"
    },
    "max_score": 1,
    "hits": [
      {
        "_index": "users",
        "_id": "_DTGlZoBr2OXTjfP9GON",
        "_score": 1,
        "_source": {
          "name": "Alice",
          "age": 20,
          "is_active": true
        }
      },
      {
        "_index": "users",
        "_id": "_TTGlZoBr2OXTjfP_2MB",
        "_score": 1,
        "_source": {
          "name": "Bob",
          "age": 20,
          "is_active": true
        }
      }
    ]
  }
}
```

### 도큐먼트 조회 (ID로 조회)

```bash
GET /users/_doc/1
```

### 도큐먼트 UPSERT (UPDATE + INSERT)

ID에 해당하는 데이터가 있으면 업데이트, 없으면 삽입합니다:

```bash
PUT /users/_doc/2
Content-Type: application/json

{"name": "Son", "age": 7, "is_active": true}
```

같은 ID로 다시 요청하면 업데이트됩니다:

```bash
PUT /users/_doc/2
Content-Type: application/json

{"name": "Son", "age": 8, "is_active": true}
```

### 도큐먼트 부분 수정

전체 도큐먼트를 대체하지 않고 특정 필드만 수정합니다:

```bash
POST /users/_update/1
Content-Type: application/json

{
  "doc": {
    "age": 10,
    "is_active": false
  }
}
```

### 도큐먼트 삭제

```bash
DELETE /users/_doc/2
```

---

## 검색 기능

### 검색용 인덱스 생성

유연한 검색 기능을 사용하려면 필드를 `text` 타입으로 정의해야 합니다:

```bash
PUT /products
Content-Type: application/json

{
  "mappings": {
    "properties": {
      "name": {
        "type": "text"
      }
    }
  }
}
```

**주의**: Elasticsearch의 문자열 타입은 `keyword`와 `text`가 있습니다. 유연한 검색 기능을 활용하려면 반드시 `text` 타입을 사용해야 합니다.

### 검색 테스트 데이터 삽입

```bash
POST /products/_doc
Content-Type: application/json

{
  "name": "Apple 2025 맥북 에어 13 M4 10코어"
}
```

### Match 쿼리를 사용한 검색

```bash
GET /products/_search
Content-Type: application/json

{
  "query": {
    "match": {
      "name": "맥북 에어 13 M4"
    }
  }
}
```

단어의 순서가 바뀌어도 검색됩니다:

```bash
GET /products/_search
Content-Type: application/json

{
  "query": {
    "match": {
      "name": "맥북 에어 M4"
    }
  }
}
```

---

## 역인덱스 (Inverted Index)

역인덱스는 Elasticsearch의 빠른 검색을 가능하게 하는 핵심 기술입니다. 필드 값을 단어 단위로 분해하여 정리한 목록입니다.

### 역인덱스 생성 과정

다음과 같이 상품을 저장한다고 가정합니다:

```bash
POST /products/_create/1
{"name": "Apple 2025 맥북 에어 13 M4 10코어"}

POST /products/_create/2
{"name": "Apple 2024 에어팟 4세대"}

POST /products/_create/3
{"name": "Apple 2024 아이패드 mini Pro"}
```

### 1단계: 단어 단위로 분해

Elasticsearch는 내부적으로 각 문서를 토큰으로 분해합니다:

- `"Apple 2025 맥북 에어 13 M4 10코어"` → `["Apple", "2025", "맥북", "에어", "13", "M4", "10코어"]`
- `"Apple 2024 에어팟 4세대"` → `["Apple", "2024", "에어팟", "4세대"]`
- `"Apple 2024 아이패드 mini Pro"` → `["Apple", "2024", "아이패드", "mini", "Pro"]`

### 2단계: 역인덱스 구축

`products` 인덱스의 `name` 필드에 대한 역인덱스:

| 토큰(Token) | 도큐먼트 ID |
|-------------|-------------|
| Apple | [1, 2, 3] |
| 2025 | [1] |
| 맥북 | [1] |
| 에어 | [1] |
| 13 | [1] |
| M4 | [1] |
| 10코어 | [1] |
| 2024 | [2, 3] |
| 에어팟 | [2] |
| 4세대 | [2] |
| 아이패드 | [3] |
| mini | [3] |
| Pro | [3] |

**주의**: 생성된 역인덱스를 직접 확인할 수는 없습니다. 시스템 내부적으로만 관리됩니다.

### 검색 시 역인덱스 활용

`"Apple 2024 아이패드"` 검색 시:

1. 검색어를 토큰으로 분해: `["Apple", "2024", "아이패드"]`
2. 역인덱스에서 일치하는 토큰 확인:
   - `Apple`: 도큐먼트 [1, 2, 3]
   - `2024`: 도큐먼트 [2, 3]
   - `아이패드`: 도큐먼트 [3]

3. 도큐먼트별 일치 점수 계산:
   - 도큐먼트 1: 1개 토큰 일치
   - 도큐먼트 2: 2개 토큰 일치
   - 도큐먼트 3: 3개 토큰 일치 (가장 관련성 높음)

### 관련성 점수 계산 로직

Elasticsearch는 다음 요소를 고려하여 점수를 계산합니다:

- **TF (Term Frequency)**: 문서 내에서 검색어가 자주 등장할수록 높은 점수
- **IDF (Inverse Document Frequency)**: 전체 문서에서 희소한 단어일수록 높은 점수
- **FLN (Field Length Normalization)**: 문서가 짧을수록 높은 점수

**주의**: 역인덱스는 `text` 타입 필드에만 적용됩니다.

---

## 애널라이저 (Analyzer)

애널라이저는 문자열을 토큰으로 변환하는 장치입니다.

### 애널라이저 구성 요소

애널라이저는 3가지 구성 요소로 이루어집니다:

```
문자열 → 캐릭터 필터 → 토크나이저 → 토큰 필터 → 토큰
```

### 1. 캐릭터 필터 (Character Filter)

문자열을 토큰화하기 전에 전처리합니다.

**HTML Stripper 예시**:
```
<p>아이폰 15 사용 후기</p> → 아이폰 15 사용 후기
```

**주요 캐릭터 필터**:
- HTML Strip Filter
- Mapping Filter
- Pattern Replace Filter

### 2. 토크나이저 (Tokenizer)

문자열을 토큰으로 분리합니다.

**Standard 토크나이저 예시** (공백, `,`, `.`, `!`, `?` 등으로 분리):
```
The Brown-Foxes jumped over the roof
→ ["The", "Brown", "Foxes", "jumped", "over", "the", "roof"]
```

**주요 토크나이저**:
- Standard Tokenizer
- Classic Tokenizer
- Keyword Tokenizer
- Pattern Tokenizer

### 3. 토큰 필터 (Token Filter)

생성된 토큰을 최종 가공합니다.

**처리 예시**:

1. **Lowercase 필터** - 모두 소문자로 변환:
   ```
   ["The", "Brown", "Foxes", "jumped", "over", "the", "roof"]
   → ["the", "brown", "foxes", "jumped", "over", "the", "roof"]
   ```

2. **Stop 필터** - 의미 없는 단어 제거 (`a`, `the`, `is` 등):
   ```
   ["the", "brown", "foxes", "jumped", "over", "the", "roof"]
   → ["brown", "foxes", "jumped", "roof"]
   ```

3. **Stemmer 필터** - 단어를 원형으로 변환:
   ```
   ["brown", "foxes", "jumped", "roof"]
   → ["brown", "fox", "jump", "roof"]
   ```

### Standard Analyzer (기본 애널라이저)

Elasticsearch의 기본 애널라이저는 다음과 같이 구성됩니다:

- **캐릭터 필터**: 없음
- **토크나이저**: Standard Tokenizer
- **토큰 필터**: Lowercase Filter

### 애널라이저 동작 확인

**기본 애널라이저 테스트**:

```bash
GET /_analyze
Content-Type: application/json

{
  "text": "Apple 2024 아이패드 mini Pro",
  "analyzer": "standard"
}
```

**커스텀 구성 테스트**:

```bash
GET /_analyze
Content-Type: application/json

{
  "char_filter": [],
  "tokenizer": "standard",
  "filter": ["lowercase"],
  "text": "Apple 2024 아이패드 mini Pro"
}
```

### 인덱스에 커스텀 애널라이저 적용

```bash
PUT /products
Content-Type: application/json

{
  "settings": {
    "analysis": {
      "analyzer": {
        "products_name_analyzer": {
          "char_filter": ["html_strip"],
          "tokenizer": "standard",
          "filter": ["lowercase"]
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "name": {
        "type": "text",
        "analyzer": "products_name_analyzer"
      }
    }
  }
}
```

### 특정 필드의 애널라이저 동작 확인

```bash
GET /products/_analyze
Content-Type: application/json

{
  "field": "name",
  "text": "Apple 2025 맥북 에어 13 M4 10코어"
}
```

### 중요 개념

애널라이저에 `lowercase` 토큰 필터가 적용되면, 검색 시 검색어도 같은 애널라이저를 거쳐 비교합니다. 따라서 대문자로 검색해도 소문자로 저장된 토큰의 도큐먼트를 정확히 찾을 수 있습니다.

---

## 애널라이저 실습

이 섹션에서는 다양한 필터와 토크나이저를 직접 적용해보면서 동작을 확인합니다.

### 실습 1: HTML Strip 캐릭터 필터

HTML 태그를 제거하는 캐릭터 필터를 적용합니다.

```bash
DELETE /boards
GET /boards

PUT /boards
Content-Type: application/json

{
  "settings": {
    "analysis": {
      "analyzer": {
        "board_content_analyzer": {
          "char_filter": ["html_strip"],
          "tokenizer": "standard",
          "filter": ["lowercase"]
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "content": {
        "type": "text",
        "analyzer": "board_content_analyzer"
      }
    }
  }
}
```

테스트 데이터 삽입:

```bash
POST /boards/_doc
Content-Type: application/json

{
  "content": "<h1>Running cats, jumpping quickly - over the lazy dogs!</h1>"
}
```

HTML 태그로 검색해도 결과가 나오지 않는지 확인:

```bash
GET /boards/_search
Content-Type: application/json

{
  "query": {
    "match": {
      "content": "h1"
    }
  }
}
```

애널라이저 동작 확인:

```bash
GET /boards/_analyze
Content-Type: application/json

{
  "field": "content",
  "text": "<h1>Running cats, jumpping quickly - over the lazy dogs!</h1>"
}
```

**결과**: HTML 태그가 제거되고 텍스트만 토큰화됩니다.

### 실습 2: Stop 토큰 필터

의미 없는 단어(the, and, is 등)를 제거하는 Stop 필터를 적용합니다.

```bash
DELETE /boards
PUT /boards
Content-Type: application/json

{
  "settings": {
    "analysis": {
      "analyzer": {
        "board_content_analyzer": {
          "char_filter": [],
          "tokenizer": "standard",
          "filter": ["lowercase", "stop"]
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "content": {
        "type": "text",
        "analyzer": "board_content_analyzer"
      }
    }
  }
}
```

테스트 데이터 삽입:

```bash
POST /boards/_doc
Content-Type: application/json

{
  "content": "The cat and the dog are friends"
}
```

검색 테스트:

```bash
GET /boards/_search
Content-Type: application/json

{
  "query": {
    "match": {
      "content": "friends"
    }
  }
}
```

애널라이저 동작 확인:

```bash
GET /boards/_analyze
Content-Type: application/json

{
  "field": "content",
  "text": "The cat and the dog are friends"
}
```

**결과**: `the`, `and`, `are` 같은 불용어가 제거되고 의미 있는 단어만 토큰으로 생성됩니다.

### 실습 3: Stemmer 토큰 필터

단어를 어근으로 변환하는 Stemmer 필터를 적용합니다.

```bash
DELETE /boards
PUT /boards
Content-Type: application/json

{
  "settings": {
    "analysis": {
      "analyzer": {
        "board_content_analyzer": {
          "char_filter": [],
          "tokenizer": "standard",
          "filter": ["lowercase", "stemmer"]
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "content": {
        "type": "text",
        "analyzer": "board_content_analyzer"
      }
    }
  }
}
```

테스트 데이터 삽입:

```bash
POST /boards/_doc
Content-Type: application/json

{
  "content": "Running cats, jumpping!"
}
```

검색 테스트:

```bash
GET /boards/_search
Content-Type: application/json

{
  "query": {
    "match": {
      "content": "jumpping"
    }
  }
}
```

애널라이저 동작 확인:

```bash
GET /boards/_analyze
Content-Type: application/json

{
  "field": "content",
  "text": "Running cats, jumpping!"
}
```

**결과**: `Running` → `run`, `cats` → `cat`, `jumpping` → `jump`으로 어근 형태로 변환됩니다.

### 실습 4: Synonym 토큰 필터

동의어를 정의하여 다양한 표현으로 검색할 수 있도록 합니다.

```bash
DELETE /products
PUT /products
Content-Type: application/json

{
  "settings": {
    "analysis": {
      "filter": {
        "products_synonym_filter": {
          "type": "synonym",
          "synonyms": [
            "notebook, 노트북, 랩탑, 휴대용 컴퓨터, laptop",
            "samsung, 삼성"
          ]
        }
      },
      "analyzer": {
        "products_name_analyzer": {
          "char_filter": [],
          "tokenizer": "standard",
          "filter": [
            "lowercase",
            "products_synonym_filter"
          ]
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "name": {
        "type": "text",
        "analyzer": "products_name_analyzer"
      }
    }
  }
}
```

테스트 데이터 삽입:

```bash
POST /products/_doc
Content-Type: application/json

{
  "name": "Samsung Notebook"
}
```

동의어로 검색:

```bash
GET /products/_search
Content-Type: application/json

{
  "query": {
    "match": {
      "name": "삼성"
    }
  }
}
```

애널라이저 동작 확인:

```bash
GET /products/_analyze
Content-Type: application/json

{
  "field": "name",
  "text": "Samsung Notebook"
}
```

**결과**: `Samsung`을 `삼성`으로 검색해도 동일한 문서를 찾을 수 있습니다.

### 실습 5: Nori 애널라이저 (한국어 형태소 분석)

한국어 텍스트를 효과적으로 분석하기 위해 Nori 플러그인을 사용합니다.

#### 1단계: Nori 플러그인 설치

`Dockerfile`을 생성합니다:

```dockerfile
FROM docker.elastic.co/elasticsearch/elasticsearch:8.17.4

RUN bin/elasticsearch-plugin install analysis-nori
```

#### 2단계: Docker Compose 파일 수정

`compose.yml` 파일을 수정합니다:

```yaml
services:
  elastic:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - 9200:9200
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=false
      - xpack.security.http.ssl.enabled=false
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
      
  kibana:
    image: docker.elastic.co/kibana/kibana:8.17.4
    ports:
      - 5601:5601
    environment:
      - ELASTICSEARCH_HOSTS=["http://elastic:9200"]
```

#### 3단계: 컨테이너 재빌드 및 실행

```bash
docker compose down
docker compose build
docker compose up -d
```

#### 4단계: Nori 애널라이저 테스트

기본 Nori 애널라이저 동작 확인:

```bash
GET /_analyze
Content-Type: application/json

{
  "text": "백화점에서 쇼핑을 하다가 친구를 만났다.",
  "analyzer": "nori"
}
```

커스텀 Nori 구성 테스트:

```bash
GET /_analyze
Content-Type: application/json

{
  "text": "백화점에서 쇼핑을 하다가 친구를 만났다.",
  "char_filter": [],
  "tokenizer": "nori_tokenizer",
  "filter": ["nori_part_of_speech", "nori_readingform", "lowercase"]
}
```

#### 5단계: Nori 애널라이저를 사용하는 인덱스 생성

```bash
DELETE /boards
PUT /boards
Content-Type: application/json

{
  "settings": {
    "analysis": {
      "analyzer": {
        "board_content_analyzer": {
          "char_filter": [],
          "tokenizer": "nori_tokenizer",
          "filter": [
            "nori_part_of_speech",
            "nori_readingform",
            "lowercase"
          ]
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "content": {
        "type": "text",
        "analyzer": "board_content_analyzer"
      }
    }
  }
}
```

테스트 데이터 삽입:

```bash
POST /boards/_doc
Content-Type: application/json

{
  "content": "백화점에서 쇼핑을 하다가 친구를 만났다."
}
```

검색 테스트:

```bash
GET /boards/_search
Content-Type: application/json

{
  "query": {
    "match": {
      "content": "백화점"
    }
  }
}
```

애널라이저 동작 확인:

```bash
GET /boards/_analyze
Content-Type: application/json

{
  "field": "content",
  "text": "백화점에서 쇼핑을 하다가 친구를 만났다."
}
```

**결과**: 한국어 문장이 형태소 단위로 정확하게 분석됩니다.

### 실습 6: 복합 필터 적용 (Nori + Stop + Stemmer)

한국어와 영어가 혼합된 텍스트를 효과적으로 분석합니다.

```bash
DELETE /boards
PUT /boards
Content-Type: application/json

{
  "settings": {
    "analysis": {
      "analyzer": {
        "board_content_analyzer": {
          "char_filter": [],
          "tokenizer": "nori_tokenizer",
          "filter": [
            "nori_part_of_speech",
            "nori_readingform",
            "lowercase",
            "stop",
            "stemmer"
          ]
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "content": {
        "type": "text",
        "analyzer": "board_content_analyzer"
      }
    }
  }
}
```

애널라이저 동작 확인:

```bash
GET /boards/_analyze
Content-Type: application/json

{
  "field": "content",
  "text": "오늘 영어 책에서 'It depends on the result.'이라는 문구를 봤다."
}
```

**결과**: 한국어는 형태소 분석이 되고, 영어는 불용어 제거 및 어근 변환이 적용됩니다.

---

## Spring Boot 연동

이 프로젝트는 Spring Boot 3와 Elasticsearch를 연동한 예제입니다.

### 프로젝트 구성

**기술 스택**:
- Java 21
- Spring Boot 3.5.7
- Spring Data Elasticsearch
- Lombok

### 의존성 설정

`build.gradle`:

```gradle
dependencies {
  implementation 'org.springframework.boot:spring-boot-starter-data-elasticsearch'
  implementation 'org.springframework.boot:spring-boot-starter-web'
  compileOnly 'org.projectlombok:lombok'
  annotationProcessor 'org.projectlombok:lombok'
}
```

### 환경 설정

`application.yaml`:

```yaml
spring:
  elasticsearch:
    uris: http://localhost:9200
```

### Document 클래스 정의

```java
@Getter
@Setter
@Document(indexName = "users")
public class User {
    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String name;

    @Field(type = FieldType.Integer)
    private Integer age;

    @Field(type = FieldType.Boolean)
    private Boolean isActive;

    @Builder
    public User(String id, String name, Integer age, Boolean isActive) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.isActive = isActive;
    }
}
```

### Repository 인터페이스

```java
public interface UserDocumentRepository extends ElasticsearchRepository<User, String> {
}
```

### 실행 방법

1. Elasticsearch 컨테이너 실행:
   ```bash
   docker compose up -d
   ```

2. Spring Boot 애플리케이션 실행:
   ```bash
   ./gradlew bootRun
   ```

---

## 주요 기능

Elasticsearch는 다음과 같은 강력한 검색 기능을 제공합니다:

1. **동의어를 고려한 검색**: 유사한 의미의 단어도 검색 가능
2. **오타를 고려한 검색**: fuzzy 쿼리를 통한 유연한 검색
3. **검색어 자동 완성**: Completion Suggester 기능
4. **관련성 기반 정렬**: 검색어와 가장 관련성이 높은 순서로 결과 반환

---

## FAQ

### Q1. keyword와 text 타입의 차이점은?

- **keyword**: 정확한 일치 검색에 사용. 분석되지 않고 원본 그대로 저장됩니다.
- **text**: 전문 검색에 사용. 애널라이저를 통해 토큰화되어 저장됩니다.

### Q2. 역인덱스를 직접 확인할 수 있나요?

아니요. 역인덱스는 내부적으로만 관리되며 직접 조회할 수 없습니다. 대신 `_analyze` API를 통해 애널라이저의 동작을 확인할 수 있습니다.

### Q3. 인덱스 생성 후 매핑을 변경할 수 있나요?

기존 필드의 타입은 변경할 수 없습니다. 새로운 필드는 추가할 수 있지만, 매핑을 변경하려면 인덱스를 재생성해야 합니다.

### Q4. UPSERT와 UPDATE의 차이점은?

- **UPSERT** (`PUT /index/_doc/id`): 도큐먼트 전체를 대체합니다. 없으면 생성합니다.
- **UPDATE** (`POST /index/_update/id`): 특정 필드만 수정합니다. 도큐먼트가 없으면 에러가 발생합니다.

---

## 참고 자료

- [Elasticsearch 공식 문서](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html)
- [Spring Data Elasticsearch](https://spring.io/projects/spring-data-elasticsearch)
- [ELK Stack 가이드](https://www.elastic.co/what-is/elk-stack)

---
