# When-Where_Project
> 목차
> - [기획](#기획)
> - [요구사항](#요구사항-명세)
> - [설계](#설계)

> 기술 스택
> - Server : Java Spring Boot 3.1.5
> - DB : MariaDB
> - Cloud : AWS EC2 ubuntu 20.04 LTS
> - 배포관리 : Docker ver 26.1.1


## 기획

### 주요 기능
  - 각 출발지 간의 최단거리를 구하여 알려주는 기능
  - 최단거리 근방의 놀거리 및 맛집 추천
  - 만날 수 있는 날짜 및 시간 관리

### 개발 전략
- 모임 시간 추천 기능
    - 월별, 주별 일정 기입 : 각 사용자들의 큰 일정들을 관리하도록 월/주간 단위로 정해진 일정 기입
    - 그룹별 스케줄 확인 : 그룹 단위로 각 인원들의 스케줄을 확인할 수 있음 → 특정 인원을 체크 해제하면 그 인원을 제외한 일정을 확인할 수 있음
    - 모임 가능 시간 추천 : 체크된 인원들의 스케줄을 확인하고 만날 공간의 특성(특정 행사 등등) 을 파악하여 모임 시간을 추천 → 체크되지 않은 인원에 대한 계산 오차가 생길 수 있으므로 만날 공간의 특성을 우선적으로 계산하고 보여주는 것이 바람직할 것
      
## 요구사항 명세

##### 사용자 기능
> - User는 아이디, 비밀번호, 닉네임(성명), 지역 등을 가진다.
> - User는 위의 정보를 기입하여 회원가입을 할 수 있다.
> - User는 카카오 계정으로 회원가입 및 로그인을 할 수 있다.
> - User는 회원가입 시에 닉네임, 지역을 입력해야 한다.
> - User는 아이디, 비밀번호 외의 정보를 내 정보 페이지에서 수정할 수 있다.
> - User는 내 정보 페이지에서 내 개인 정보를 확인할 수 있다.
> - User는 내 정보 페이지에서 계정 탈퇴를 할 수 있다.

##### 그룹 기능(생성 기능)
> - Group은 그룹 명, 호스트(FK), 모임 특징 등을 가진다.
> - Group의 모임 특징은 운동, 낚시, 게임, 음식 등이 있다.
> - Group의 호스트는 User가 Group 생성 시에 자동으로 정해진다.
> - User의 Group 생성 가능 횟수는 3회까지 가능하다.
> - Group의 호스트는 Group의 특징 및 그룹 명을 변경할 수 있다.

##### 그룹 기능(신청 및 관리 기능)
> - 호스트는 카카오톡 메신저를 통해 그룹 초대 메시지를 보낼 수 있다.
> - 메시지 전송 시 카카오 API를 활용하여 카카오톡 친구인 사람에게만 보낸다.
> - 메시지를 받은 인원은 해당 메시지를 클릭하여 서비스로 접근이 가능하다.
> - 모임 인원은 접근 시에 카카오 계정으로 회원가입 또는 로그인이 진행된다.
> - 모임 인원은 로그인 이후에 그룹 신청 양식이 주어진다.
> - 모임 인원은 신청 양식을 작성하고 신청 버튼을 눌러 신청할 수 있다.
> - 호스트는 로그인 시에 그룹 관리 페이지에서 그룹 신청서를 확인할 수 있다.
> - 호스트는 신청서를 접수 또는 반려할 수 있다.
> - 그룹원이 된 User는 그룹 관리 페이지에서 그룹원을 관리할 수 있다.
> - Group에 가입할 수 있는 인원은 최대 8명이다. (테스트 과정에서는 4명만 가능)

##### 스케줄 기능(개인)
> - Schedule은 제목, 상세 내용, 시작 시간, 끝 시간, 스케줄 주인(FK)을 가진다.
> - 시작 및 끝 시간은 해당 스케줄이 시작하는 시간과 끝나는 시간을 의미한다.
> - Schedule은 User가 스케줄 관리 페이지에서 생성할 수 있다.
> - Schedule은 생성 시에 위 내용을 모두 입력해야 한다.
> - Schedule 조회 시 해당 날짜에 세로 막대로 표시된다.
> - 막대는 시작 시간, 끝 시간에 따라 길이가 바뀐다.
> - 개인 스케줄 추가 시 이미 있는 스케줄과 겹칠 수 없다.

##### 스케줄 기능(그룹)
> - Schedule의 그룹 조회 시 기본적으로 그룹 인원 전체와 조회하는 주를 기준으로 조회한다.
> - Schedule의 그룹 조회 시 조회하는 날짜를 지정할 수 있다.
> - Schedule의 그룹 조회 시 특정 인원들을 조회할 수 있다.
> - Schedule 조회 시 인원들의 스케줄이 겹치게 되면 하나로 합친 막대로 표현한다.
> - 빈 시간 추천 기능 사용 시 조회된 스케줄 시간을 제외한 시간을 추천해준다.
> - 추천 시에 해당 시간의 막대는 버튼으로 표현된다.
> - 버튼을 누르면 해당 시간으로 모임을 정할 수 있다.
> - 빈 시간 추천 조건은 스케줄 간 최소 30분 이상이며 제한되는 시간이 있다.
> - 최소 시간과 제한되는 시간은 그룹에서 정할 수 있으며, 직접 기입도 가능하다.


## 설계

### DB 설계

<img width="747" alt="스크린샷 2023-11-21 오후 10 04 18" src="https://github.com/user-attachments/assets/4e3dfe7a-a626-493e-92fb-b64fdc122a3e">



### API 명세

#### 사용자 API

###### 유저 조회

| METHOD | URL | DESCRIPTION |
| --- | --- | --- |
| GET | /api/user/get-user | 유저 단일 조회 |

```json

Request Header
{
	"Authorization" : "Bearer ${TOKEN}"
}

Response
{
	"data" : [
		"id" : "유저 PK",
		"userId" : "유저 아이디",
		"password" : NULL,
		"location" : "유저 주소",
		"nickname" : "유저 닉네임",
		"activated": "계정 활성화 여부"
	],
	"status" : 200
}
```

###### 유저 수정

| METHOD | URL | DESCRIPTION |
| --- | --- | --- |
| POST | /api/user/modify | 개인 정보 수정 |

```json
Request Header
{
	"Authorization" : "Bearer ${TOKEN}"
}
Request // 없으면 "" 또는 NULL
{
	"userId" : "유저 아이디",
	"password" : "비밀번호",
	"location" : "유저 주소"
}

Response
{
	"status" : 200,
	"message" : "OK"
}
```

###### 회원 탈퇴

| METHOD | URL | DESCRIPTION |
| --- | --- | --- |
| POST | /api/user/delete | 회원 탈퇴 |

```json
Request Header
{
	"Authorization" : "Bearer ${TOKEN}"
}
Response
{
	"status" : 200,
	"message" : "OK"
}
```

###### 회원 가입

| METHOD | URL | DESCRIPTION |
| --- | --- | --- |
| POST | /api/user/sign-up | 회원 가입 |

```json
Request
{
	"userId" : "유저 아이디",
	"password" : "비밀번호",
	"nickname" : "닉네임",
	"location" : "주소"
}

Response
{
	"status" : 201,
	"message" : "CREATED"
}
```

###### 로그인

| METHOD | URL | DESCRIPTION |
| --- | --- | --- |
| POST | /api/user/auth | 로그인 |

```json
Request
{
	"userId" : "유저 아이디",
	"password" : "비밀번호"
}

Response
{
	"token" : JWT_TOKEN,
	"status" : 200,
	"message" : "OK"
}
```


#### 스케줄 API

###### 스케줄 조회

| METHOD | URL | DESCRIPTION |
| --- | --- | --- |
| GET | /api/schedule/get-schedule | 개인 스케줄 조회 |

```json
Request Header
{
	"Authorization" : "Bearer ${TOKEN}"
}

Response
{
	"data" : 
	[
		{
			"id" : "스케줄 PK",
			"title" : "스케줄 제목",
			"startTime" : "시작 시간",
			"endTime" : "종료 시간"
		}
	]
	"status" : 200,
	"message" : "OK"
}
```

###### 스케줄 추가

| METHOD | URL | DESCRIPTION |
| --- | --- | --- |
| POST | /api/schedule/add | 개인 스케줄 추가 |

```json
Request Header
{
	"Authorization" : "Bearer ${TOKEN}"
}

Request
{
	"title" : "스케줄 제목",
	"detail" : "스케줄 내용",
	"startTime" : "시작 시간",
	"endTime" : "종료 시간"
}

Response
{
	"status" : 201,
	"message" : "CREATED"
}
```

###### 빈 시간 계산

| METHOD | URL | DESCRIPTION |
| --- | --- | --- |
| POST | /api/schedule/busytime-group | 그룹 단위 빈 시간 계산 |

```json
Request Header
{
	"Authorization" : "Bearer ${TOKEN}"
}

Request
{
	"members" : ["유저PK", ...],
	"startDate" : "시작 날짜",
	"endDate" : "종료 날짜"
}

Response
{
	"data" : 
	[
		{
			"title" : "스케줄 제목",
			"detail" : "스케줄 내용",
			"startTime" : "시작 시간",
			"endTime" : "종료 시간"
		}, ...
	],
	"status" : 200,
	"message" : "OK"
}
```

###### 스케줄 수정

| METHOD | URL | DESCRIPTION |
| --- | --- | --- |
| POST | /api/schedule/modify | 스케줄 수정 |

```json
Request Header
{
	"Authorization" : "Bearer ${TOKEN}"
}
Request
{
	"id" : "수정할 스케줄 PK",
	"title" : "수정할 스케줄의 제목",
	"detail" : "수정할 스케줄의 내용"
}
Response
{
	"token" : JWT_TOKEN,
	"status" : 200,
	"message" : "OK"
}
```

###### 스케줄 삭제

| METHOD | URL | DESCRIPTION |
| --- | --- | --- |
| POST | /api/schedule/delete | 스케줄 삭제 |

```json
Request Header
{
	"Authorization" : "Bearer ${TOKEN}"
}
Request
{
	"id" : "스케줄 PK"
}

Response
{
	"status" : 200,
	"message" : "OK"
}
```
#### 그룹 API

###### 그룹 생성

| METHOD | URL | DESCRIPTION |
| --- | --- | --- |
| POST | /api/group/create | 그룹 추가 |

```json
Request Header
{
	"Authorization" : "Bearer ${TOKEN}"
}

Request
{
	"groupName" : "그룹 이름",
	"attribute" : "그룹 속성"
}

Response
{
	"status" : 201,
	"message" : "CREATED"
}
```

###### 내 그룹 조회

| METHOD | URL | DESCRIPTION |
| --- | --- | --- |
| POST | /api/group/get-my-groups | 그룹 추가 |

```json
Request Header
{
	"Authorization" : "Bearer ${TOKEN}"
}

Request
{
	"groupName" : "그룹 이름",
	"attribute" : "그룹 속성"
}

Response
{
	"status" : 201,
	"message" : "CREATED"
}
```

###### 그룹 구성원 조회

| METHOD | URL | DESCRIPTION |
| --- | --- | --- |
| GET | /group/get-members | 그룹의 모든 구성원 조회 |

```json
Request Header
{
	"Authorization" : "Bearer ${TOKEN}"
}

Params
{
	"gorupId" : "그룹 PK"
}

Response
{
	"status" : 200,
	"data" : [
		{
			"id" : "유저 PK",
			"userId" : "아이디",
			"nickname" : "닉네임"
		}, ...
	],
	"message" : "OK"
}
```

###### 그룹 수정

| METHOD | URL | DESCRIPTION |
| --- | --- | --- |
| POST | /group/modify | 수정 |

```json
Request Header
{
	"Authorization" : "Bearer ${TOKEN}"
}

Request
{
	"id" : "그룹 PK",
	"groupName" : "그룹 이름",
	"attribute" : "그룹 특성"
}

Response
{
	"status" : 200,
	"message" : "OK"
}
```

###### 그룹 삭제

| METHOD | URL | DESCRIPTION |
| --- | --- | --- |
| POST | /group/delete | 그룹 삭제 |

```json
Request Header
{
	"Authorization" : "Bearer ${TOKEN}"
}

Request
{
	"id" : "그룹 PK"
}

Response
{
	"status" : 200,
	"message" : "OK"
}
```

#### 지원서 API

###### 지원서 제출

| METHOD | URL | DESCRIPTION |
| --- | --- | --- |
| POST | /apply/add-apply | 그룹 지원서 제출 |

```json
Request Header
{
	"Authorization" : "Bearer ${TOKEN}"
}

Request
{
	"applyGroupId" : "지원할 그룹 PK"
}

Response
{
	"status" : 200,
	"message" : "OK"
}
```

###### 그룹 지원서 조회

| METHOD | URL | DESCRIPTION |
| --- | --- | --- |
| GET | /apply/get-apply | 제출된 그룹 지원서 전체 조회 |

```json
Request Header
{
	"Authorization" : "Bearer ${TOKEN}"
}

Params
{
	"groupId" : "그룹 PK"
}

Response
{
	"status" : 200,
	"data" : [
		{
			"applyId" : "지원서 PK",
			"id" : "유저 PK",
			"userId" : "아이디",
			"nickname" : "닉네임"
		}, ...
	],
	"message" : "OK"
}
```

###### 지원서 처리

| METHOD | URL | DESCRIPTION |
| --- | --- | --- |
| POST | /process | 그룹에 제출된 지원서 처리 |

```json
Request Header
{
	"Authorization" : "Bearer ${TOKEN}"
}

Request
{
	"id" : "지원서 PK",
	"decide" : "승인 및 반려 여부"
}

Response
{
	"status" : 200,
	"message" : "OK"
}
```

###### 지원서 삭제

| METHOD | URL | DESCRIPTION |
| --- | --- | --- |
| POST | /delete | 본인의 지원서 삭제 |

```json
Request Header
{
	"Authorization" : "Bearer ${TOKEN}"
}

Request
{
	"id" : "지원서 PK"

Response
{
	"status" : 200,
	"message" : "OK"
}
```

---



