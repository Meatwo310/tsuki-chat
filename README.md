[Modrinth](https://modrinth.com/mod/tsuki-chat) | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/tsuki-chat) | [GitHub](https://github.com/Meatwo310/tsuki-chat/)

# Tsuki Chat

A server-side MOD that converts Romaji to Japanese in chat. Highly inspired by the LunaChat plugin.  
チャットに送信されたローマ字を自動的に日本語に変換するサーバーサイドのMOD。LunaChatに強く影響されています。

![banner](https://github.com/Meatwo310/tsuki-chat/assets/72017364/bf0137b5-94d6-4a6f-8a58-7549dda8d4b0)

## Features
### en_us

- Converts Romaji sent in chat to Hiragana and transliterate to Japanese using Google CGI API.
- Replaces `&` with `§`. Interpret basic markdown.
- Ignore messages that already contain Japanese or begin with a configurable prefix.
- Use `/tsukichat` to toggle personal settings.
- Edit config to fine-tune the features.

### ja_jp

- 送信されたローマ字をひらがなに変換し、Google CGI APIを用いて非同期で日本語に変換します。
- `&`を`§`に置き換えます。また、簡単なMarkdownを解釈します。
- 既に日本語を含むメッセージや、設定したプレフィックスで始まるメッセージを無視します。
- `/tsukichat`で個人設定を切り替えます。
- コンフィグで機能を微調整できます。

## Images
![image](https://github.com/Meatwo310/tsuki-chat/assets/72017364/723c4b15-985a-45be-bc78-83fc5a4792d7)
![image](https://github.com/Meatwo310/tsuki-chat/assets/72017364/f9937988-58c9-4eb2-a5cd-910993d631c1)
![image](https://github.com/Meatwo310/tsuki-chat/assets/72017364/1121273f-86e2-416e-a3d2-a93bbde03209)
![image](https://github.com/Meatwo310/tsuki-chat/assets/72017364/e1504aca-d139-4476-aca2-4578cbccdf58)
![image](https://github.com/Meatwo310/tsuki-chat/assets/72017364/79fee958-1c91-44b1-94e3-0165b5440f59)

## Technical Information
This is a client-side mod, so you only need to put this on the server. 
This mod will work in the single player world if you put this in the client.

This project is released under the MIT License.  

You are free to add this mod to your Modrinth/CurseForge modpack.
You can also use the jar file directly if you give credit.

The project is split into branches.
The `main` branch has some metafiles.
The `${MODLOADER}-${VERSION}` branch contains the source code of this mod.
