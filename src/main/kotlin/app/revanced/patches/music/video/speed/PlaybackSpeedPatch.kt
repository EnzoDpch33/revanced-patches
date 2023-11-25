package app.revanced.patches.music.video.speed

import app.revanced.extensions.exception
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patches.music.utils.overridespeed.OverrideSpeedHookPatch
import app.revanced.patches.music.utils.settings.SettingsPatch
import app.revanced.patches.music.video.speed.fingerprints.PlaybackSpeedBottomSheetFingerprint
import app.revanced.patches.music.video.speed.fingerprints.PlaybackSpeedBottomSheetParentFingerprint
import app.revanced.util.bytecode.BytecodeHelper.updatePatchStatus
import app.revanced.util.enum.CategoryType
import app.revanced.util.integrations.Constants.MUSIC_VIDEO_PATH
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction

@Patch(
    name = "Remember playback speed",
    description = "Save the playback speed value whenever you change the playback speed.",
    dependencies = [
        OverrideSpeedHookPatch::class,
        SettingsPatch::class
    ],
    compatiblePackages = [
        CompatiblePackage(
            "com.google.android.apps.youtube.music",
            [
                "6.21.52",
                "6.27.54",
                "6.28.52"
            ]
        )
    ]
)
@Suppress("unused")
object PlaybackSpeedPatch : BytecodePatch(
    setOf(PlaybackSpeedBottomSheetParentFingerprint)
) {
    override fun execute(context: BytecodeContext) {

        PlaybackSpeedBottomSheetParentFingerprint.result?.let { parentResult ->
            PlaybackSpeedBottomSheetFingerprint.also {
                it.resolve(
                    context,
                    parentResult.classDef
                )
            }.result?.let {
                it.mutableMethod.apply {
                    val targetIndex = it.scanResult.patternScanResult!!.startIndex
                    val targetRegister =
                        getInstruction<FiveRegisterInstruction>(targetIndex).registerD

                    addInstruction(
                        targetIndex,
                        "invoke-static {v$targetRegister}, $INTEGRATIONS_CLASS_DESCRIPTOR->userChangedSpeed(F)V"
                    )
                }
            } ?: throw PlaybackSpeedBottomSheetFingerprint.exception
        } ?: throw PlaybackSpeedBottomSheetParentFingerprint.exception

        SettingsPatch.addMusicPreference(
            CategoryType.VIDEO,
            "revanced_enable_save_playback_speed",
            "true"
        )

        context.updatePatchStatus("RememberPlaybackSpeed", false)

    }

    private const val INTEGRATIONS_CLASS_DESCRIPTOR =
        "$MUSIC_VIDEO_PATH/PlaybackSpeedPatch;"
}