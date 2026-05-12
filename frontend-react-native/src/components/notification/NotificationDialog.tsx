import { Ionicons } from '@expo/vector-icons';
import React from 'react';
import { Modal, Text, TouchableOpacity, View } from 'react-native';

interface NotificationProps {
    title: string;
    message?: string;
    type?: 'success' | 'error' | 'warning' | 'info';
    onClose: (result: boolean) => void;
}

export default function NotificationDialog({ title, message, type = 'info', onClose }: NotificationProps) {
    const typeConfig = {
        success: { icon: "checkmark-circle", color: "#10b981", bgColor: "bg-green-50", btnColor: "bg-green-600" },
        error: { icon: "alert-circle", color: "#ef4444", bgColor: "bg-red-50", btnColor: "bg-red-600" },
        warning: { icon: "warning", color: "#f59e0b", bgColor: "bg-amber-50", btnColor: "bg-amber-600" },
        info: { icon: "information-circle", color: "#3b82f6", bgColor: "bg-blue-50", btnColor: "bg-blue-600" }
    } as const;

    const config = typeConfig[type as keyof typeof typeConfig] || typeConfig.info;

    return (
        <Modal transparent animationType="fade" visible={true}>
            <View className="flex-1 justify-center items-center bg-black/50 p-6">
                <View className="bg-white w-full max-w-[320px] rounded-2xl shadow-xl overflow-hidden">
                    <View className="flex-row justify-between items-start p-5">
                        <View className="flex-row items-center flex-1">
                            <View className={`p-2 rounded-xl ${config.bgColor} mr-3`}>
                                <Ionicons name={config.icon as any} size={24} color={config.color} />
                            </View>
                            <Text className="text-xl font-bold text-gray-800 flex-1">{title}</Text>
                        </View>
                        <TouchableOpacity onPress={() => onClose(false)}>
                            <Ionicons name="close" size={24} color="#9ca3af" />
                        </TouchableOpacity>
                    </View>

                    <View className="px-5 pb-5">
                        <Text className="text-gray-500 text-md leading-5">{message}</Text>
                    </View>

                    <View className="p-4 border-t border-gray-50 flex-row justify-end">
                        <TouchableOpacity
                            onPress={() => onClose(true)}
                            className={`px-7 py-[11px] rounded-xl ${config.btnColor}`}
                        >
                            <Text className="text-white font-bold">Đồng ý</Text>
                        </TouchableOpacity>
                    </View>
                </View>
            </View>
        </Modal>
    );
}