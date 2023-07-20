@echo off

for /r %%f in (asm-debug-all-5.2.jar) do (
 	if exist %%f (
		echo %%f
		for /r %USERPROFILE% %%r in (asm-debug-all-4.1.jar) do (
			if exist %%r (
				echo %%r
				xcopy /s /y "%%f" "%%r"
				break
			)
		)
	)
)

echo Patched
pause
