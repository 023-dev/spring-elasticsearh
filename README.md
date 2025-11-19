# Elasticsearch

## 개요

Elasticsearch는 오픈 소스 분산, RESTful 검색 및 분석 엔진입니다. 
확장 가능한 데이터 저장소이자 벡터 데이터베이스로 활용됩니다.

### 주요 활용 사례

Elasticsearch는 크게 2가지 용도로 활용됩니다.

#### 1. 데이터 수집 및 분석

로그와 같은 대규모 데이터를 수집하고 분석하는 데 최적화되어 있습니다. 
일반적으로 다음 구성 요소들과 함께 사용됩니다:

- **Elasticsearch**: 데이터 저장
- **Logstash**: 데이터 수집 및 가공
- **Kibana**: 데이터 시각화

#### 2. 검색 최적화

- 대규모 데이터에서도 뛰어난 검색 속도 제공
- 오타나 동의어를 고려한 유연한 검색 기능 지원

## 배경

전통적인 관계형 데이터베이스는 대규모 텍스트 검색과 실시간 분석에 한계가 있습니다. 
Elasticsearch는 다음 문제들을 해결하기 위해 등장했습니다:

- **검색 성능**: 대용량 데이터에서도 빠른 전문 검색(Full-text Search) 제공
- **유연한 검색**: 오타 허용, 동의어 처리, 자동완성 등 고급 검색 기능 지원
- **확장성**: 분산 아키텍처로 수평 확장 가능
- **실시간 분석**: 로그 및 메트릭 데이터의 실시간 수집과 분석

## 환경 설정

### 사전 요구사항

- Docker 및 Docker Compose 설치
- 최소 4GB 이상의 메모리 권장

### Elasticsearch 설치

#### 1단계: 디렉터리 생성

```bash
mkdir docker-elasticsearch
cd docker-elasticsearch
```

#### 2단계: docker-compose.yml 파일 작성

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
```

#### 3단계: 컨테이너 구동

```bash
docker compose up
```

#### 4단계: 설치 확인

Postman 또는 curl을 사용하여 다음 요청을 보냅니다:

```bash
curl -X GET "localhost:9200"
```

예상 응답:

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

### Kibana 설치

Kibana는 Elasticsearch를 위한 GUI 도구입니다. 기본 포트는 5601입니다.

#### 1단계: docker-compose.yml 수정

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

#### 2단계: 컨테이너 구동

```bash
docker compose up -d
```

#### 3단계: Kibana 접속

브라우저에서 `localhost:5601`에 접속합니다.

## 핵심 개념

### MySQL과의 비교

Elasticsearch의 주요 개념을 MySQL과 비교하여 이해할 수 있습니다:

| 개념 | MySQL | Elasticsearch |
|------|-------|---------------|
| 데이터 저장소 | 데이터베이스 | 인덱스(Index) |
| 스키마 정의 | 스키마(Schema) | 매핑(Mapping) |
| 데이터 단위 | 레코드/로우(Row) | 도큐먼트(Document) |
| 컬럼 | 컬럼(Column) | 필드(Field) |

**공통점**: 데이터를 저장, 조회, 수정, 삭제할 수 있습니다.

### 기본 작동 방식

Elasticsearch는 REST API를 통해 통신합니다.

#### 데이터 삽입 비교

**MySQL**:
```sql
INSERT INTO users (name, email) VALUES ('John', 'test1234@example.com');
```

**Elasticsearch**:
```bash
curl -X POST "localhost:9200/users/_doc" \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "John",
    "email": "test1234@example.com"
  }'
```

#### 데이터 조회 비교

**MySQL**:
```sql
SELECT * FROM users;
```

**Elasticsearch**:
```bash
curl -X GET "localhost:9200/users/_search" \
  -H 'Content-Type: application/json' \
  -d '{
    "query": {
      "match_all": {}
    }
  }'
```

### 주요 기능

Elasticsearch는 다음과 같은 고급 검색 기능을 제공합니다:

- **동의어 검색**: 유사한 의미의 단어로 검색 가능
- **오타 허용 검색**: 철자 오류가 있어도 검색 가능
- **자동완성**: 검색어 자동완성 기능
- **관련성 순위**: 검색어와 가장 관련성이 높은 순서대로 결과 정렬

## 실습 가이드

### 인덱스 관리

#### 인덱스 생성

```json
PUT /users
```

#### 인덱스 조회

```json
GET /users
```

인덱스가 존재하지 않으면 `404 - Not Found`를 반환합니다.

#### 인덱스 삭제

```json
DELETE /users
```

#### 매핑 정의

매핑은 인덱스에 저장될 데이터의 타입을 정의합니다:

```json
PUT /users/_mappings
{
  "properties": {
    "name": { "type": "keyword" },
    "age": { "type": "integer" },
    "is_active": { "type": "boolean" }
  }
}
```

성공 시 `200 - OK`와 함께 `"acknowledged": true`가 반환됩니다.

### 도큐먼트 관리

#### 도큐먼트 삽입 (자동 ID 생성)

```json
POST /users/_doc
{
  "name": "Alice",
  "age": 20,
  "is_active": true
}
```

#### 도큐먼트 삽입 (명시적 ID 지정)

```json
POST /users/_create/1
{
  "name": "Hee",
  "age": 30,
  "is_active": true
}
```

**참고**: 이미 존재하는 ID로 생성 시도 시 `409 - Conflict` 오류가 발생합니다.

#### 전체 도큐먼트 조회

```json
GET /users/_search
```

응답 예시:

```json
{
  "took": 209,
  "timed_out": false,
  "_shards": {
    "total": 1,
    "successful": 1,
    "skipped": 0,
    "failed": 0
  },
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

#### 특정 도큐먼트 조회

```json
GET /users/_doc/1
```

#### 도큐먼트 UPSERT (UPDATE + INSERT)

ID에 해당하는 데이터가 있으면 업데이트하고, 없으면 새로 삽입합니다:

```json
PUT /users/_doc/2
{
  "name": "Son",
  "age": 7,
  "is_active": true
}
```

동일한 ID로 다시 실행하면 업데이트됩니다:

```json
PUT /users/_doc/2
{
  "name": "Son",
  "age": 8,
  "is_active": true
}
```

#### 도큐먼트 부분 수정

```json
POST /users/_update/1
{
  "doc": {
    "age": 10,
    "is_active": false
  }
}
```

#### 도큐먼트 삭제

```json
DELETE /users/_doc/2
```

### 검색 실습

#### 검색용 인덱스 생성

유연한 검색 기능을 활용하려면 필드 타입을 `text`로 지정해야 합니다:

```json
PUT /products
{
  "mappings": {
    "properties": {
      "name": { "type": "text" }
    }
  }
}
```

**참고**: Elasticsearch는 문자열에 대해 `keyword`와 `text` 두 가지 타입을 지원합니다. `text` 타입은 전문 검색을 위해 토큰화되지만, `keyword` 타입은 정확히 일치하는 검색에만 사용됩니다.

#### 도큐먼트 삽입

```json
POST /products/_doc
{
  "name": "Apple 2025 맥북 에어 13 M4 10코어"
}
```

#### 검색 실행

```json
GET /products/_search
{
  "query": {
    "match": {
      "name": "맥북 에어 13 M4"
    }
  }
}
```

`"name": "맥북 에어 M4"`로 검색해도 관련 도큐먼트를 찾을 수 있습니다.

## 심화 개념

### 역인덱스(Inverted Index)

역인덱스는 Elasticsearch의 빠른 검색을 가능하게 하는 핵심 메커니즘입니다.

#### 역인덱스란?

역인덱스는 필드 값을 단어(토큰)별로 분해하여 저장하는 색인 구조입니다. 각 토큰이 어떤 도큐먼트에 포함되어 있는지를 매핑합니다.

#### 역인덱스 생성 과정

다음 도큐먼트들을 저장한다고 가정해봅시다:

```json
POST /products/_create/1
{
  "name": "Apple 2025 맥북 에어 13 M4 10코어"
}

POST /products/_create/2
{
  "name": "Apple 2024 에어팟 4세대"
}

POST /products/_create/3
{
  "name": "Apple 2024 아이패드 mini Pro"
}
```

**1단계: 토큰화**

각 문자열을 단어 단위로 분리합니다:

- `"Apple 2025 맥북 에어 13 M4 10코어"` → `["Apple", "2025", "맥북", "에어", "13", "M4", "10코어"]`
- `"Apple 2024 에어팟 4세대"` → `["Apple", "2024", "에어팟", "4세대"]`
- `"Apple 2024 아이패드 mini Pro"` → `["Apple", "2024", "아이패드", "mini", "Pro"]`

**2단계: 역인덱스 구축**

| 토큰(Token) | 도큐먼트 ID |
|------------|-----------|
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

**참고**: 생성된 역인덱스는 시스템 내부적으로만 관리되며 직접 확인할 수 없습니다.

#### 검색 시 역인덱스 활용

`"Apple 2024 아이패드"`로 검색하면:

1. 검색어를 토큰화: `["Apple", "2024", "아이패드"]`
2. 각 토큰과 일치하는 도큐먼트 확인:
   - ID 1: "Apple" 1개 일치
   - ID 2: "Apple", "2024" 2개 일치
   - ID 3: "Apple", "2024", "아이패드" 3개 일치
3. 일치하는 토큰이 많을수록 높은 점수 부여

#### 관련성 점수 계산

Elasticsearch는 다음 요소들을 고려하여 점수를 계산합니다:

- **TF (Term Frequency)**: 문서 내에서 검색어가 자주 등장할수록 높은 점수
- **IDF (Inverse Document Frequency)**: 전체 문서에서 희귀한 단어일수록 높은 점수
- **FLN (Field Length Normalization)**: 문서가 짧을수록 높은 점수

이러한 역인덱스 구조 덕분에 단어의 순서와 관계없이 관련 도큐먼트를 효율적으로 검색할 수 있습니다.

**중요**: 역인덱스는 `text` 타입 필드에만 적용됩니다.

### 애널라이저(Analyzer)

애널라이저는 문자열을 토큰으로 변환하는 장치입니다.

#### 애널라이저 구성 요소

애널라이저는 다음 세 가지 구성 요소로 이루어집니다:

```
문자열 입력
    ↓
[캐릭터 필터] - 문자열 전처리
    ↓
[토크나이저] - 토큰으로 분리
    ↓
[토큰 필터] - 토큰 후처리
    ↓
최종 토큰 출력
```

#### 1. 캐릭터 필터(Character Filter)

문자열을 토큰화하기 전에 전처리합니다.

**HTML Stripper 필터 예시**:
```
<p>아이폰 15 사용 후기</p> → 아이폰 15 사용 후기
```

기타 캐릭터 필터:
- **Mapping Filter**: 특정 문자를 다른 문자로 변환
- **Pattern Replace Filter**: 정규표현식 패턴으로 문자 치환

#### 2. 토크나이저(Tokenizer)

문자열을 토큰으로 분리합니다.

**Standard 토크나이저 예시** (공백, 쉼표, 마침표 등으로 분리):
```
The Brown-Foxes jumped over the roof
→ [The, Brown, Foxes, jumped, over, the, roof]
```

기타 토크나이저:
- **Classic Tokenizer**: 이메일, URL 등을 인식
- **Keyword Tokenizer**: 전체 문자열을 하나의 토큰으로 처리
- **Pattern Tokenizer**: 정규표현식 패턴으로 분리

#### 3. 토큰 필터(Token Filter)

토큰을 최종적으로 정제합니다.

**처리 단계 예시**:

1. **Lowercase 필터 적용**:
   ```
   [The, Brown, Foxes, jumped, over, the, roof]
   → [the, brown, foxes, jumped, over, the, roof]
   ```

2. **Stop 필터 적용** (불용어 제거):
   ```
   [the, brown, foxes, jumped, over, the, roof]
   → [brown, foxes, jumped, roof]
   ```

3. **Stemmer 필터 적용** (어간 추출):
   ```
   [brown, foxes, jumped, roof]
   → [brown, fox, jump, roof]
   ```

#### Standard Analyzer

Elasticsearch의 기본 애널라이저는 Standard Analyzer입니다:

- **캐릭터 필터**: 없음
- **토크나이저**: Standard Tokenizer
- **토큰 필터**: Lowercase Filter

동작: 공백이나 문장 부호를 기준으로 분리한 후 소문자로 변환합니다.

#### 애널라이저 동작 확인

**일반 애널라이저 테스트**:
```json
GET /_analyze
{
  "text": "Apple 2024 아이패드 mini Pro",
  "analyzer": "standard"
}
```

**커스텀 구성 테스트**:
```json
GET /_analyze
{
  "char_filter": [],
  "tokenizer": "standard",
  "filter": ["lowercase"],
  "text": "Apple 2024 아이패드 mini Pro"
}
```

#### 커스텀 애널라이저 적용

인덱스에 커스텀 애널라이저를 정의할 수 있습니다:

```json
PUT /products
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

#### 인덱스의 애널라이저 동작 확인

```json
GET /products/_analyze
{
  "field": "name",
  "text": "Apple 2025 맥북 에어 13 M4 10코어"
}
```

**중요**: 애널라이저에 lowercase 토큰 필터가 적용되면 검색어도 동일한 애널라이저를 거쳐 비교됩니다. 따라서 대문자로 검색해도 소문자로 저장된 토큰을 찾을 수 있습니다.

## 애널라이저 실습

### HTML Strip 캐릭터 필터 실습

HTML 태그를 제거하는 `html_strip` 필터의 동작을 확인해봅시다.

#### 인덱스 초기화 및 생성

```json
DELETE /boards

PUT /boards
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

#### 도큐먼트 삽입

```json
POST /boards/_doc
{
  "content": "<h1>Running cats, jumpping quickly - over the lazy dogs!</h1>"
}
```

#### HTML 태그로 검색 시도

```json
GET /boards/_search
{
  "query": {
    "match": {
      "content": "h1"
    }
  }
}
```

**결과**: HTML 태그는 제거되었으므로 검색되지 않습니다.

#### 애널라이저 동작 확인

```json
GET /boards/_analyze
{
  "field": "content",
  "text": "<h1>Running cats, jumpping quickly - over the lazy dogs!</h1>"
}
```

**확인 사항**: HTML 태그가 제거되고 순수 텍스트만 토큰화됩니다.

### Stop 토큰 필터 실습

의미 없는 불용어(a, the, is 등)를 제거하는 `stop` 필터의 동작을 확인해봅시다.

#### 인덱스 재생성

```json
DELETE /boards

PUT /boards
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

#### 도큐먼트 삽입

```json
POST /boards/_doc
{
  "content": "The cat and the dog are friends"
}
```

#### 검색 실행

```json
GET /boards/_search
{
  "query": {
    "match": {
      "content": "friends"
    }
  }
}
```

#### 애널라이저 동작 확인

```json
GET /boards/_analyze
{
  "field": "content",
  "text": "The cat and the dog are friends"
}
```

**확인 사항**: "the", "and", "are" 같은 불용어가 제거되고 "cat", "dog", "friends"만 토큰으로 생성됩니다.

### Stemmer 토큰 필터 실습

단어를 원형으로 변환하는 `stemmer` 필터의 동작을 확인해봅시다.

#### 인덱스 재생성

```json
DELETE /boards

PUT /boards
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

#### 도큐먼트 삽입

```json
POST /boards/_doc
{
  "content": "Running cats, jumpping!"
}
```

#### 검색 실행

```json
GET /boards/_search
{
  "query": {
    "match": {
      "content": "jumpping"
    }
  }
}
```

#### 애널라이저 동작 확인

```json
GET /boards/_analyze
{
  "field": "content",
  "text": "Running cats, jumpping!"
}
```

**확인 사항**: "Running" → "run", "cats" → "cat", "jumpping" → "jump"로 어간이 추출됩니다.

### Synonym 토큰 필터 실습

동의어를 처리하는 `synonym` 필터의 동작을 확인해봅시다.

#### 동의어 필터가 포함된 인덱스 생성

```json
DELETE /products

PUT /products
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

#### 도큐먼트 삽입

```json
POST /products/_doc
{
  "name": "Samsung Notebook"
}
```

#### 동의어로 검색

```json
GET /products/_search
{
  "query": {
    "match": {
      "name": "삼성"
    }
  }
}
```

**결과**: "Samsung"으로 저장했지만 동의어인 "삼성"으로도 검색 가능합니다.

#### 애널라이저 동작 확인

```json
GET /products/_analyze
{
  "field": "name",
  "text": "Samsung Notebook"
}
```

**확인 사항**: "Samsung"과 "Notebook"이 각각의 동의어들로 확장됩니다.

### Nori 한국어 분석기 실습

한국어 형태소 분석을 위한 Nori 플러그인을 사용해봅시다.

#### 사전 준비: Nori 플러그인 설치

**1단계: Dockerfile 작성**

```dockerfile
FROM docker.elastic.co/elasticsearch/elasticsearch:8.17.4

RUN bin/elasticsearch-plugin install analysis-nori
```

**2단계: docker-compose.yml 수정**

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

**3단계: 컨테이너 재구동**

```bash
docker compose down
docker compose up -d --build
```

#### Nori 애널라이저 기본 동작 확인

```json
GET /_analyze
{
  "text": "백화점에서 쇼핑을 하다가 친구를 만났다.",
  "analyzer": "nori"
}
```

#### Nori 애널라이저 구성 요소 확인

```json
GET /_analyze
{
  "text": "백화점에서 쇼핑을 하다가 친구를 만났다.",
  "char_filter": [],
  "tokenizer": "nori_tokenizer",
  "filter": ["nori_part_of_speech", "nori_readingform", "lowercase"]
}
```

**Nori 구성 요소**:
- **nori_tokenizer**: 한국어를 형태소 단위로 분리
- **nori_part_of_speech**: 특정 품사 필터링
- **nori_readingform**: 한자를 한글 발음으로 변환

#### Nori 애널라이저를 사용하는 인덱스 생성

```json
DELETE /boards

PUT /boards
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

#### 한국어 도큐먼트 삽입

```json
POST /boards/_doc
{
  "content": "백화점에서 쇼핑을 하다가 친구를 만났다."
}
```

#### 한국어 검색

```json
GET /boards/_search
{
  "query": {
    "match": {
      "content": "백화점"
    }
  }
}
```

#### 애널라이저 동작 확인

```json
GET /boards/_analyze
{
  "field": "content",
  "text": "백화점에서 쇼핑을 하다가 친구를 만났다."
}
```

**확인 사항**: 한국어가 형태소 단위로 정확하게 분리됩니다.

### 복합 필터 실습 (한영 혼용 텍스트)

한국어와 영어가 혼용된 텍스트에 여러 필터를 조합해봅시다.

#### 복합 애널라이저 인덱스 생성

```json
DELETE /boards

PUT /boards
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

#### 한영 혼용 텍스트 분석

```json
GET /boards/_analyze
{
  "field": "content",
  "text": "오늘 영어 책에서 'It depends on the result.'이라는 문구를 봤다."
}
```

**확인 사항**: 
- 한국어는 형태소 분석
- 영어는 불용어 제거 및 어간 추출
- 모든 텍스트가 소문자로 변환

## 다음 단계

Elasticsearch를 더 깊이 학습하려면:

- 공식 문서의 [Aggregation](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations.html) 기능 학습
- [Logstash](https://www.elastic.co/logstash)와 [Kibana](https://www.elastic.co/kibana)를 활용한 ELK 스택 구축
- 실제 프로젝트에 검색 기능 적용해보기

